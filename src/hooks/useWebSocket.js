import { useState, useEffect, useRef, useCallback } from 'react';
import { apiClient, endpoints } from '../config/api';

export function useWebSocket(topic = '/topic/timing', restFallbackEndpoint = endpoints.liveSession) {
  const [data, setData] = useState(null);
  const [connectionStatus, setConnectionStatus] = useState('Connecting'); // Connecting, Connected, Reconnecting, Offline
  const [isUsingFallback, setIsUsingFallback] = useState(false);
  const wsRef = useRef(null);
  const pollIntervalRef = useRef(null);
  const reconnectCountRef = useRef(0);

  const fetchRestFallback = useCallback(async () => {
    try {
      const res = await apiClient.get(restFallbackEndpoint);
      if (res && res.data) {
        setData(res.data);
      }
    } catch (err) {
      console.warn('[RacePulse REST Fallback Error]', err.message);
    }
  }, [restFallbackEndpoint]);

  const connect = useCallback(() => {
    setConnectionStatus('Connecting');
    setIsUsingFallback(false);

    try {
      // Establish WebSocket connection (native WS fallback or SockJS)
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      const host = import.meta.env.VITE_WS_URL || `${protocol}//localhost:8080/ws-raw`;

      const ws = new WebSocket(host);
      wsRef.current = ws;

      ws.onopen = () => {
        setConnectionStatus('Connected');
        setIsUsingFallback(false);
        reconnectCountRef.current = 0;
        if (pollIntervalRef.current) {
          clearInterval(pollIntervalRef.current);
          pollIntervalRef.current = null;
        }
      };

      ws.onmessage = (event) => {
        try {
          const parsed = JSON.parse(event.data);
          setData(parsed);
        } catch (e) {
          // If non-JSON string
          setData(event.data);
        }
      };

      ws.onerror = () => {
        setConnectionStatus('Reconnecting');
      };

      ws.onclose = () => {
        if (reconnectCountRef.current < 3) {
          reconnectCountRef.current += 1;
          setConnectionStatus('Reconnecting');
          setTimeout(() => connect(), 3000);
        } else {
          // Switch to REST fallback if WS fails after retries
          setConnectionStatus('Offline');
          setIsUsingFallback(true);
          fetchRestFallback();
          pollIntervalRef.current = setInterval(fetchRestFallback, 5000);
        }
      };
    } catch (err) {
      console.warn('[RacePulse WS Error]', err.message);
      setConnectionStatus('Offline');
      setIsUsingFallback(true);
      fetchRestFallback();
      pollIntervalRef.current = setInterval(fetchRestFallback, 5000);
    }
  }, [fetchRestFallback]);

  useEffect(() => {
    connect();
    return () => {
      if (wsRef.current) {
        wsRef.current.close();
      }
      if (pollIntervalRef.current) {
        clearInterval(pollIntervalRef.current);
      }
    };
  }, [connect]);

  return { data, connectionStatus, isUsingFallback, refetchFallback: fetchRestFallback };
}

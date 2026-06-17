import { Client } from '@stomp/stompjs';

const WS_URL = '/ws-native';

export class GameWebSocket {
  constructor() {
    const isHttps = window.location.protocol === 'https:';
    const wsProtocol = isHttps ? 'wss:' : 'ws:';
    
    this.client = new Client({
      brokerURL: `${wsProtocol}//${window.location.host}${WS_URL}`,
      debug: function (str) {
        // console.log(str); // Uncomment for debugging
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 0,
      heartbeatOutgoing: 0,
    });
  }

  connect(onStateUpdate, onSyncUpdate, onConnect) {
    this.client.onConnect = () => {
      if (onConnect) onConnect(); // Trigger graph refresh on connection/reconnection
      this.client.subscribe('/topic/game', (message) => {
        if (message.body) {
          onStateUpdate(JSON.parse(message.body));
        }
      });
      this.client.subscribe('/topic/game/sync', (message) => {
        if (message.body) {
          if (onSyncUpdate) onSyncUpdate(JSON.parse(message.body));
        }
      });
    };

    this.client.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };

    this.client.activate();
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
    }
  }
}

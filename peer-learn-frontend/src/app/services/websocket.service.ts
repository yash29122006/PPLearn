import { Injectable, inject } from '@angular/core';

import {
  Client,
  IMessage,
  StompSubscription
} from '@stomp/stompjs';

import { Observable } from 'rxjs';

import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private client: Client | null = null;
private readonly authService = inject(AuthService);
  private readonly subscriptions: StompSubscription[] = [];

  connect(): Promise<void> {

    if (this.client?.connected) {
      return Promise.resolve();
    }

    return new Promise<void>((resolve, reject) => {

      this.client = new Client({
  brokerURL: 'ws://localhost:8080/ws',

  connectHeaders: {
    Authorization: `Bearer ${this.authService.getToken()}`
  },

  reconnectDelay: 5000,

  debug: () => {}
});

      this.client.onConnect = () => {

        console.log('[WebSocket] Connected');

        resolve();
      };

      this.client.onStompError = (frame) => {

        console.error(
          '[WebSocket] STOMP error:',
          frame.headers['message'],
          frame.body
        );

      };

      this.client.onWebSocketError = (error) => {

        console.error(
          '[WebSocket] WebSocket error:',
          error
        );

        reject(error);
      };

      this.client.onWebSocketClose = () => {

        console.log(
          '[WebSocket] Connection closed'
        );

      };

      this.client.activate();
    });
  }

  disconnect(): void {

    this.subscriptions.forEach(
      subscription => subscription.unsubscribe()
    );

    this.subscriptions.length = 0;

    if (this.client) {

      this.client.deactivate();

      this.client = null;
    }
  }

  subscribe<T>(
    destination: string
  ): Observable<{
    type: string;
    data: T;
  }> {

    return new Observable<{
      type: string;
      data: T;
    }>(subscriber => {

      if (!this.client?.connected) {

        subscriber.error(
          new Error(
            'WebSocket is not connected'
          )
        );

        return;
      }

      const subscription =
        this.client.subscribe(
          destination,
          (message: IMessage) => {

            try {

              const event = JSON.parse(
                message.body
              ) as {
                type: string;
                data: T;
              };

              subscriber.next(event);

            } catch (error) {

              subscriber.error(error);

            }

          }
        );

      this.subscriptions.push(subscription);

      return () => {

        subscription.unsubscribe();

        const index =
          this.subscriptions.indexOf(
            subscription
          );

        if (index !== -1) {
          this.subscriptions.splice(index, 1);
        }

      };

    });
  }
}
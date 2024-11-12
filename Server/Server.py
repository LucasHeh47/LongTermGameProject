import socket
import threading
import random
import json
import time

from Party import Party
from Player import Player

active_parties = []
players_online = []

def generate_random_string(length, characters):
    return ''.join(random.choice(characters) for _ in range(length))


class GameServer(threading.Thread):
    def __init__(self, port=1331):
        super().__init__()
        self.port = port
        try:
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            self.socket.bind(('', self.port))
            print(f"Server started on port {self.port}")
        except socket.error as e:
            print(f"Socket error: {e}")

    def run(self):
        while True:
            print("Running")
            print("Active Players: " + str(len(players_online)))
            print("Active Parties: " + str(len(active_parties)))
            data, address = self.socket.recvfrom(1024)
            message = data.decode('utf-8').strip()

            try:
                request = json.loads(message)  # Decode JSON
            except json.JSONDecodeError:
                print("Received malformed JSON packet")
                self.send_data(b"Malformed JSON", address)
                continue

            if request.get("request") == "login":
                auth_token = generate_random_string(32, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
                response = json.dumps({"auth_token": auth_token}).encode('utf-8')
                newPlayer = Player(request["username"], address, auth_token)
                players_online.append(newPlayer)
                print(f"Received 'login' from {address}, sending auth token.")
                self.send_data(response, address)

            elif request.get("request") == "logout":
                auth_token = request.get("auth_token")
                print(f"Received 'logout' from {address}")
                print(f"Searching player with: {auth_token}")
                # Find the player in players_online by auth_token
                player_to_remove = next((player for player in players_online if player.auth_token == auth_token), None)

                if player_to_remove:
                    players_online.remove(player_to_remove)
                    print(f"Player {player_to_remove.username} removed from online players.")
                    response = json.dumps({"status": "Success"}).encode('utf-8')
                else:
                    print("Player not found for logout.")
                    response = json.dumps({"status": "Player not found"}).encode('utf-8')

                self.send_data(response, address)

            elif request.get("request") == "Create Party?":
                auth_token = request.get("auth_token")
                print(f"Party creation request from {address}")
                newParty = Party()
                newParty.add_player(self.get_player(auth_token))
                active_parties.append(newParty)
                response = json.dumps({"status": "Party Created"}).encode('utf-8')
                self.send_data(response, address)

            elif request.get("request") == "Party Info":
                auth_token = request.get("auth_token")
                player = self.get_player(auth_token)
                party = player.party
                if party is None:
                    response = json.dumps({"Party Info": "Not in party"})
                else:
                    response = json.dumps({
                        "Party Info": {
                            "Host": party.get_host(),
                            "Members": [str(player) for player in party.players]
                        }
                    })
                self.send_data(response, address)

            elif message.lower() == "ping":
                print(f"Received 'ping' from {address}")
                self.send_data(json.dumps({"response": "pong"}).encode('utf-8'), address)

            else:
                print(f"Received unknown command: {message}")
                error_response = json.dumps({"error": "Unknown command"})
                self.send_data(error_response.encode('utf-8'), address)


    def get_player(self, auth_token):
        return next((player for player in players_online if player.auth_token == auth_token), None)


    def send_data(self, data, address):
        try:
            self.socket.sendto(data, address)
            print("Packet sent!")
        except socket.error as e:
            print(f"Send error: {e}")


def login_and_out(server_ip, server_port, username, action):
    """
    Sends a login or logout request and waits for a response.

    Args:
    - server_ip (str): The IP address of the server.
    - server_port (int): The server's port.
    - username (str): The username for authentication.
    - action (str): "login" or "logout".

    Returns:
    - str: Auth token on login success, or "Logout successful".
    """
    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as sock:
        sock.settimeout(5)  # Timeout for receiving a response

        # Prepare packet data based on action
        if action == "login":
            packet = json.dumps({"username": username, "request": "login"}).encode('utf-8')
        elif action == "logout":
            packet = json.dumps({"username": username, "request": "logout"}).encode('utf-8')
        else:
            return "Invalid action"

        # Send packet to server
        sock.sendto(packet, (server_ip, server_port))

        # Wait for response
        try:
            response, _ = sock.recvfrom(1024)  # Adjust buffer size as needed
            response_data = json.loads(response.decode('utf-8'))

            if action == "login" and "auth_token" in response_data:
                return response_data["auth_token"]
            elif action == "logout" and response_data.get("status") == "Success":
                return "Logout successful"
            else:
                return "Unexpected response"

        except socket.timeout:
            return "No response from server"


if __name__ == "__main__":
    server = GameServer()
    server.start()


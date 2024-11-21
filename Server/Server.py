import socket
import threading
import random
import json

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
            data, address = self.socket.recvfrom(1024)
            message = data.decode('utf-8').strip()

            try:
                packet = json.loads(message)  # Decode JSON
            except json.JSONDecodeError:
                print("Received malformed JSON packet")
                self.send_data({"error": "Malformed JSON"}, address)
                continue

            # Handle packet type
            packet_type = packet.get("packet_type")
            if not packet_type:
                print("Packet missing 'packet_type'")
                self.send_data({"error": "Missing packet_type"}, address)
                continue

            if packet_type == "login_request":
                self.handle_login(packet, address)

            elif packet_type == "logging_off":
                self.handle_logout(packet, address)

            elif packet_type == "create_party":
                self.handle_create_party(packet, address)

            elif packet_type == "host_to_clients":
                self.handle_host_to_clients(packet, address)

            elif packet_type == "join_party":
                self.handle_join_party(packet, address)

            elif packet_type == "party_going_into_game":
                self.handle_party_going_into_game(packet, address)

            elif packet_type == "player_position":
                self.handle_player_position(packet, address)

            else:
                print(f"Unknown packet type: {packet_type}")
                self.send_data({"error": "Unknown packet_type"}, address)

    def handle_login(self, packet, address):
        auth_token = generate_random_string(32, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
        new_player = Player(packet["username"], address, auth_token)
        players_online.append(new_player)
        print(f"User {packet['username']} logged in from {address}")
        self.send_data({"auth_token": auth_token, "status": "login_success"}, address)

    def handle_logout(self, packet, address):
        auth_token = packet.get("auth_token")
        player_to_remove = self.get_player(auth_token)
        if player_to_remove:
            players_online.remove(player_to_remove)
            print(f"Player {player_to_remove.username} logged out.")
            self.send_data({"status": "logout_success"}, address)
        else:
            print("Logout failed: Invalid auth_token")
            self.send_data({"error": "Invalid auth_token"}, address)

    def handle_create_party(self, packet, address):
        auth_token = packet.get("auth_token")
        player = self.get_player(auth_token)
        if player:
            new_party = Party()
            new_party.add_player(player)
            active_parties.append(new_party)
            print(f"Party created by {player.username}")
            self.send_data({"status": "party_created"}, address)
        else:
            print("Party creation failed: Invalid auth_token")
            self.send_data({"error": "Invalid auth_token"}, address)

    def handle_host_to_clients(self, packet, address):
        auth_token = packet.get("auth_token")
        player = self.get_player(auth_token)
        if not player:
            print("host_to_clients failed: Invalid auth_token")
            self.send_data({"error": "Invalid auth_token"}, address)
            return

        # Get the player's party
        party = player.party
        if not party:
            print(f"Player {player.username} is not in a party")
            self.send_data({"error": "Not in a party"}, address)
            return

        # Forward the packet to all other party members
        for member in party.players:
            if member != player:  # Don't send to the sender
                self.send_data(packet, member.address)

        print(f"Forwarded packet from {player.username} to party members")

    def handle_join_party(self, packet, address):
        auth_token = packet.get("auth_token")
        player = self.get_player(auth_token)
        if not player:
            print("join_party failed: Invalid auth_token")
            self.send_data({"error": "Invalid auth_token"}, address)
            return

        # Check if the party exists and add the player
        username_to_join = packet["data"].get("join_party")
        host_player = self.get_player_by_username(username_to_join)
        if not host_player or not host_player.party:
            print("Party not found or host invalid")
            self.send_data({"error": "Party not found"}, address)
            return

        host_player.party.add_player(player)
        print(f"Player {player.username} joined the party hosted by {host_player.username}")
        self.send_data({"status": "join_success"}, address)

    def handle_party_going_into_game(self, packet, address):
        auth_token = packet.get("auth_token")
        player = self.get_player(auth_token)
        if not player or not player.party:
            print("party_going_into_game failed: Invalid auth_token or not in a party")
            self.send_data({"error": "Invalid auth_token or not in a party"}, address)
            return

        # Notify all party members
        for member in player.party.players:
            self.send_data(packet, member.address)

        print(f"Party hosted by {player.username} is going into the game")

    def handle_player_position(self, packet, address):
        auth_token = packet.get("auth_token")
        player = self.get_player(auth_token)
        if not player or not player.party:
            print("player_position failed: Invalid auth_token or not in a party")
            self.send_data({"error": "Invalid auth_token or not in a party"}, address)
            return

        # Forward position update to all other players
        for member in player.party.players:
            if member != player:
                self.send_data(packet, member.address)

        print(f"Forwarded position update for {player.username}")

    def get_player(self, auth_token):
        return next((player for player in players_online if player.auth_token == auth_token), None)

    def get_player_by_username(self, username):
        return next((player for player in players_online if player.username == username), None)

    def send_data(self, data, address):
        try:
            if isinstance(data, dict):
                data = json.dumps(data).encode('utf-8')
            self.socket.sendto(data, address)
            print(f"Packet sent to {address}")
        except socket.error as e:
            print(f"Send error: {e}")


if __name__ == "__main__":
    server = GameServer()
    server.start()

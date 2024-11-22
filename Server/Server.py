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
            data, ip_address = self.socket.recvfrom(1024)
            message = data.decode('utf-8').strip()
            print(message)

            try:
                packet = json.loads(message)  # Decode JSON
            except json.JSONDecodeError:
                print("Received malformed JSON packet")
                self.send_data({"error": "Malformed JSON"}, ip_address)
                continue

            # Handle packet type
            packet_type = packet.get("packet_type")
            if not packet_type:
                print("Packet missing 'packet_type'")
                self.send_data({"error": "Missing packet_type"}, ip_address)
                continue

            if packet_type == "login_request":
                self.handle_login(packet, ip_address)

            elif packet_type == "logging_off":
                self.handle_logout(packet, ip_address)

            elif packet_type == "create_party":
                self.handle_create_party(packet, ip_address)

            elif packet_type == "host_to_clients":
                self.handle_host_to_clients(packet, ip_address)

            elif packet_type == "clients_to_host":
                self.handle_clients_to_host(packet, ip_address)

            elif packet_type == "picked_class":
                self.handle_class_picked(packet, ip_address)

            elif packet_type == "join_party":
                self.handle_join_party(packet, ip_address)

            elif packet_type == "party_going_into_game":
                self.handle_party_going_into_game(packet, ip_address)

            elif packet_type == "player_position":
                self.handle_player_position(packet, ip_address)

            else:
                print(f"Unknown packet type: {packet_type}")
                self.send_data({"error": "Unknown packet_type"}, ip_address)

    def handle_login(self, packet, ip_address):
        auth_token = generate_random_string(32, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
        new_player = Player(packet["username"], ip_address, auth_token)
        players_online.append(new_player)
        print(f"User {packet['username']} logged in from {ip_address}")
        self.send_data({"new_auth_token": auth_token, "status": "login_success"}, ip_address)

    def handle_logout(self, packet, ip_address):
        auth_token = packet.get("auth_token")
        player_to_remove = self.get_player(auth_token)
        if player_to_remove:
            players_online.remove(player_to_remove)
            print(f"Player {player_to_remove.username} logged out.")
            self.send_data({"status": "logout_success"}, ip_address)
        else:
            print("Logout failed: Invalid auth_token")
            self.send_data({"error": "Invalid auth_token"}, ip_address)

    def handle_create_party(self, packet, ip_address):
        auth_token = packet.get("auth_token")
        player = self.get_player(auth_token)
        if player:
            new_party = Party()
            new_party.add_player(player)  # Add the host to the party
            active_parties.append(new_party)
            player.party = new_party  # Link the player to the party
            print(f"Party created by {player.username}")
            self.send_data({"party": "party_created"}, ip_address)
        else:
            print("Party creation failed: Invalid auth_token")
            self.send_data({"error": "Invalid auth_token"}, ip_address)

    def handle_host_to_clients(self, packet, ip_address):
        auth_token = packet.get("auth_token")
        player = self.get_player(auth_token)
        if not player:
            print("host_to_clients failed: Invalid auth_token")
            self.send_data({"error": "Invalid auth_token"}, ip_address)
            return

        # Forward the packet to all other players, including the host
        party = player.party
        if not party:
            print(f"Player {player.username} is not in a party")
            self.send_data({"error": "Not in a party"}, ip_address)
            return

        for member in party.players:
            if member is player: continue
            print(f"Forwarded packet from {player.username} to {member.username}")
            self.send_data(packet, member.ip_address)  # Forward to everyone, including the sender

    def handle_clients_to_host(self, packet, ip_address):
        auth_token = packet.get("auth_token")
        player = self.get_player(auth_token)
        if not player:
            print("host_to_clients failed: Invalid auth_token")
            self.send_data({"error": "Invalid auth_token"}, ip_address)
            return

        # Forward the packet to all other players, including the host
        party = player.party
        if not party:
            print(f"Player {player.username} is not in a party")
            self.send_data({"error": "Not in a party"}, ip_address)
            return

        host = party.get_host()
        print(f"Forwarded packet from {player.username} to {host.username}")
        self.send_data(packet, host.ip_address)  # Forward to everyone, including the sender

    def handle_class_picked(self, packet, ip_address):
        auth_token = packet.get("auth_token")
        player = self.get_player(auth_token)
        if not player:
            print("class_picked failed: Invalid auth_token")
            self.send_data({"error": "Invalid auth_token"}, ip_address)
            return

        party = player.party
        if not party:
            print(f"Player {player.username} is not in a party")
            self.send_data({"error": "Not in a party"}, ip_address)
            return

        player.is_picking_class = False  # Update player's class selection state
        print(f"{player.username} picked their class.")

        # Check if all players have picked their class
        all_ready = True
        for p in player.party.players:
            print(p.username + " ready?: " + str(not p.is_picking_class))
            if p.is_picking_class: all_ready = False
        if all_ready:
            print(f"All players in party have picked their classes.")
            for p in player.party.players:
                self.send_data({"picking_class": "all_ready"}, p.ip_address)

    def handle_join_party(self, packet, ip_address):
        auth_token = packet.get("auth_token")
        player = self.get_player(auth_token)
        if not player:
            print("join_party failed: Invalid auth_token")
            self.send_data({"error": "Invalid auth_token"}, ip_address)
            return

        # Check if the party exists and add the player
        username_to_join = packet["data"].get("join_party")
        host_player = self.get_player_by_username(username_to_join)
        if not host_player or not host_player.party:
            print("Party not found or host invalid")
            self.send_data({"error": "Party not found"}, ip_address)
            return

        host_player.party.add_player(player)
        print(f"Player {player.username} joined the party hosted by {host_player.username}")
        self.send_data({"party": {"join_success": host_player.username}}, ip_address)
        self.send_data({"party": {"player_joined": player.username}}, host_player.ip_address)

    def handle_party_going_into_game(self, packet, ip_address):
        auth_token = packet.get("auth_token")
        player = self.get_player(auth_token)
        if not player or not player.party:
            print("party_going_into_game failed: Invalid auth_token or not in a party")
            self.send_data({"error": "Invalid auth_token or not in a party"}, ip_address)
            return

        # Notify all party members
        for member in player.party.players:
            self.send_data(packet, member.ip_address)

        print(f"Party hosted by {player.username} is going into the game")

    def handle_player_position(self, packet, ip_address):
        auth_token = packet.get("auth_token")
        player = self.get_player(auth_token)
        if not player or not player.party:
            print("player_position failed: Invalid auth_token or not in a party")
            self.send_data({"error": "Invalid auth_token or not in a party"}, ip_address)
            return

        # Forward position update to all other players
        for member in player.party.players:
            if member != player:
                self.send_data(packet, member.ip_address)

        print(f"Forwarded position update for {player.username}")

    def get_player(self, auth_token):
        return next((player for player in players_online if player.auth_token == auth_token), None)

    def get_player_by_username(self, username):
        return next((player for player in players_online if player.username == username), None)

    def send_data(self, data, ip_address):
        try:
            if isinstance(data, dict):
                data = json.dumps(data).encode('utf-8')
            self.socket.sendto(data, ip_address)
            print(f"Packet sent to {ip_address}")
        except socket.error as e:
            print(f"Send error: {e}")


if __name__ == "__main__":
    server = GameServer()
    server.start()

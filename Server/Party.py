


class Party:
    def __init__(self):
        self.players = []

    def add_player(self, player):
        if len(self.players) < 8:
            self.players.append(player)
            player.party = self
            return True  # Player added successfully
        else:
            print("Party is full!")
            return False  # Party is full

    def remove_player(self, player):
        if player in self.players:
            self.players.remove(player)
            player.party = None
            return True  # Player removed successfully
        else:
            print("Player not found in party!")
            return False  # Player not found

    def get_host(self):
        # The first player in the list is considered the host
        if self.players:
            return self.players[0]
        return None  # No players in the party

    def __repr__(self):
        return f"Party(host={self.get_host()}, players={self.players})"


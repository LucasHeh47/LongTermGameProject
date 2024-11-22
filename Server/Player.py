from Party import Party


class Player:
    def __init__(self, username, ip_address, auth_token):
        self.party = None
        self.username = username
        self.ip_address = ip_address
        self.auth_token = auth_token
        self.is_picking_class = True

    def set_party(self, party: Party):
        self.party = party


import socket
import json
import time
import sys

ok = 0
class SessionMessage:
    def __init__(self, number, length):
        self.number = number
        self.length = length

    def to_json(self):
        return json.dumps(dict(number=self.number,
                               length=self.length)
                          )


def create_client():
    s = socket.socket()
    s.setsockopt(socket.SOL_TCP, socket.TCP_NODELAY, 1)
    return s


def connect_server(s, addr):
    s.connect(addr)


def send_data(s, data):
    s.sendall(data)


def receive_data(s):
    global ok
    ok += 1
    data_recv = s.recv(1024)
    return data_recv == b"ok\n"


def __main():
    args = sys.argv
    number = 8192
    length = 8192
    try:
        number = int(args[1])
        length = int(args[2])
        print("package num is ", number)
        print("package length is ", length)
    except:
        print("no cli args show")
    s = create_client()
    addr = ("localhost", 5001)
    connect_server(s, addr)
    data = b"b" * (length - 1) + b"\n"
    sm = SessionMessage(number, length)
   # send_data(s, sm.to_json().encode())
    for i in range(number):
        send_data(s, data)
        f = receive_data(s)
        if not f:
            print("not ok")
            break
    time.sleep(1)
    s.close()


__main()

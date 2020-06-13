import socket
import json
import time
import sys


time_before = ""
def create_server():
    s = socket.socket()
    host = ""
    port = 5001
    s.bind((host, port))
    s.listen(5)
    return s


def deal_client(c, buffer_size):
    global time_before
    time_before = time.time()
    while True:
        data_read = read_data_n(c, buffer_size)
        if len(data_read) == 0:
            c.close()
            time_cost = time.time() - time_before
            print("time cost", time_cost)
            global number, length
            BYTES = number * length / (1024 * 1024)
            print("%s MB/s throughout" % (BYTES / time_cost))
            break
        if data_read[-1] == 10:
            c.send(b"ok")


def read_data_n(c, n):
    data_read = b""
    buffer_size = 8192
    while n > 0:
        if n < buffer_size:
            buffer_size = n
        data = c.recv(buffer_size)
        n -= buffer_size
        data_read += data
    return data_read


def listen(s, length, number):
    head_str = json.dumps(dict(length=length,
                          number=number))
    head_size = len(head_str)
    while True:
        c, addr = s.accept()
        data = c.recv(head_size)
        d = json.loads(data.decode())
        deal_client(c, length)


def __main():
    args = sys.argv
    global number, length
    number = 8192
    length = 8192
    try:
        number = int(args[1])
        length = int(args[2])
        print("package num is ", number)
        print("package length is ", length)
    except:
        print("no cli args show")
    s = create_server()
    listen(s, length, number)


__main()

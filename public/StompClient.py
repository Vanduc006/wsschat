from websocket import create_connection
import threading
import uuid
import sys

username = input("USERNAME: ")

ws = create_connection("ws://localhost:8080/hello")


def send_frame(frame):
    ws.send(frame + "\x00")


# CONNECT
send_frame(f"""CONNECT
accept-version:1.2
host:localhost
username:{username}

""")

print(ws.recv())


# SUBSCRIBE
send_frame(f"""SUBSCRIBE
id:{uuid.uuid4()}
destination:/queue/messages-user{username}
ack:auto

""")

print(f"Connected as {username}")


# RECEIVE THREAD
def receive_loop():
    while True:
        try:
            msg = ws.recv()

            sys.stdout.write(
                f"\n\n========== RECEIVED ==========\n{msg}\n==============================\n"
            )

            sys.stdout.flush()

        except Exception as e:
            print("Disconnected:", e)
            break


# INPUT THREAD
def input_loop():
    while True:
        try:
            target = input("TO: ")

            send_frame(f"""SEND
destination:/app/hello
content-type:text/plain

{target}
""")

        except KeyboardInterrupt:
            break


threading.Thread(target=receive_loop, daemon=True).start()
input_loop()
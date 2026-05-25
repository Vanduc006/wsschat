import { useEffect, useState } from "react"

import { Input } from "./components/ui/input"
import { Button } from "./components/ui/button"

import StompProvider from "./StompProvider"
import { useStomp } from "./hooks/useStomp"

type ChatType = {
    to: string
    message: string
    username: string
}

function ChatApp() {

    const {
        connected,
        subscribe,
        send
    } = useStomp()

    const [username, setUsername] =
        useState("")

    const [connectedUser,
        setConnectedUser] =
        useState("")

    const [to, setTo] =
        useState("")

    const [message, setMessage] =
        useState("")

    const [chat, setChat] =
        useState<ChatType[]>([])

    useEffect(() => {

        if (
            !connected ||
            !connectedUser
        ) {
            return
        }

        subscribe(
            `/queue/messages-user${connectedUser}`,

            (msg) => {

                setChat((prev) => [

                    ...prev,

                    {
                        to: connectedUser,
                        username: "server",
                        message: msg.body
                    }
                ])
            }
        )

    }, [
        connected,
        connectedUser,
        subscribe
    ])

    const handleConnect = () => {

        setConnectedUser(username)
    }

    const handleSend = () => {

        send(
            "/app/hello",
            to
        )

        setChat((prev) => [

            ...prev,

            {
                to,
                username:
                    connectedUser,
                message
            }
        ])

        setMessage("")
    }

    return (

        <div className="m-5">

            <div className="flex gap-2 mt-5">

                <div>
                    Your name
                </div>

                <Input
                    value={username}
                    onChange={(e) =>
                        setUsername(
                            e.target.value
                        )
                    }
                />

                <Button
                    onClick={
                        handleConnect
                    }
                >
                    Connect
                </Button>

            </div>

            <div className="flex gap-2 mt-5">

                <div>To</div>

                <Input
                    value={to}
                    onChange={(e) =>
                        setTo(
                            e.target.value
                        )
                    }
                />

            </div>

            <div className="flex gap-2 mt-5">

                <div>
                    Message
                </div>

                <Input
                    value={message}
                    onChange={(e) =>
                        setMessage(
                            e.target.value
                        )
                    }
                />

            </div>

            <Button
                className="mt-5"
                onClick={handleSend}
            >
                Send
            </Button>

            <div className="mt-10 space-y-2">

                {chat.map(
                    (item, index) => (

                        <div
                            key={index}
                            className="border p-2 rounded"
                        >

                            <div>
                                from:
                                {" "}
                                {
                                    item.username
                                }
                            </div>

                            <div>
                                to:
                                {" "}
                                {item.to}
                            </div>

                            <div>
                                {
                                    item.message
                                }
                            </div>

                        </div>
                    )
                )}

            </div>

        </div>
    )
}

export default function App() {

    const [username, setUsername] =
        useState("duc")

    return (

        <StompProvider
            username={username}
        >

            <ChatApp />

        </StompProvider>
    )
}
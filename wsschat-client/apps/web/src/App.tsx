import { useEffect, useRef, useState } from "react"

import { Input } from "./components/ui/input"
import { Button } from "./components/ui/button"

import StompProvider from "./StompProvider"
import { useStomp } from "./hooks/useStomp"

type ChatType = {
    to: string
    message: string
    username: string
}

type TypingType = {
    username: string,
    isTyping: false
}

type ChatAppProps = {
    // username: string,
    connectedUser: string,
}

function ChatApp({
    connectedUser
}: ChatAppProps) {

    // console.log
    const {
        connected,
        subscribe,
        send
    } = useStomp()

    const [username, setUsername] =
        useState(connectedUser)

    // const [connectedUser,
    //     setConnectedUser] =
    //     useState("")

    const [to, setTo] =
        useState("")

    const [message, setMessage] =
        useState("")

    const [chat, setChat] =
        useState<ChatType[]>([])

    const [typing, setTyping] = useState<TypingType | undefined>()
    const typingTimeoutRef = useRef<NodeJS.Timeout | null>(null)

    useEffect(() => {

        if (
            !connected
        ) {
            return
        }

        subscribe(
            `/queue/messages-user${connectedUser}`,
            (msg) => {
                // console.log("New message")
                const data = JSON.parse(msg.body)
                // console.log(data)

                switch (data.eventType) {
                    case "CHAT":
                        setChat((prev) => [
                            ...prev,
                            {
                                to: data.payload.receiverUsername,
                                username: data.payload.senderUsername,
                                message: data.payload.messageContent
                            }
                        ])
                        break

                    case "TYPING":
                        console.log(data.payload)
                        const typingEvent = {
                            username: data.payload.senderUsername,
                            isTyping: data.payload.isTyping
                        } as TypingType
                        setTyping(typingEvent)
                        if (typingTimeoutRef.current) {
                            clearTimeout(typingTimeoutRef.current)
                        }
                        typingTimeoutRef.current = setTimeout(() => {
                            setTyping({
                                username: typingEvent.username,
                                isTyping: false
                            })
                        }, 500)
                        break
                }

            }
        )

    }, [
        connected,
        username,
        subscribe
    ])

    // const handleConnect = () => {
    //     setConnectedUser(username)
    // }

    const handleSend = () => {
        if (!to) {
            return;
        }
        const payload = {
            senderUsername: connectedUser,
            receiverUsername: to,
            messageContent: message,
        }
        send(
            "/app/chat.send",
            payload
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

    const handleTyping = () => {
        if (!to || !message) {
            return;
        }
        console.log("typing ...")
        const payload = {
            senderUsername: connectedUser,
            receiverUsername: to,
            isTyping: true
        }
        send("/app/chat.typing", payload)
    }

    return (

        <div>

            {/* <Button
                onClick={
                    handleConnect
                }
            >
                Connect
            </Button> */}
            <div>hello {connectedUser} !</div>
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
                    onChange={(e) => {
                        setMessage(e.target.value)
                        handleTyping()
                    }}
                />

            </div>

            <Button
                className="mt-5"
                onClick={handleSend}
            >
                Send
            </Button>

            <div>
                {typing?.isTyping ?
                    <div>
                        {typing?.username} is typing ...
                    </div> :
                    <div></div>
                }
            </div>

            <div className="mt-10 space-y-2">
                {chat.map((item, index) => {
                    const isCurrentUser = item.username === connectedUser;

                    return (
                        <div
                            key={index}
                            className={`flex w-full ${isCurrentUser ? 'justify-end' : 'justify-start'}`}
                        >
                            {isCurrentUser ? (
                                <div className="w-fit border p-2 rounded-lg text-right">
                                    <span className="block text-xs opacity-75 mb-1">{item.username}</span>
                                    <p className="text-sm font-medium">{item.message}</p>
                                </div>
                            ) : (
                                <div className="w-fit border p-2 rounded-lg bg-blue-200">
                                    <span className="block text-xs text-gray-500 mb-1">{item.username}</span>
                                    <p className="text-sm font-medium">{item.message}</p>
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>


        </div>
    )
}

export default function App() {

    const [username, setUsername] =
        useState("")

    const [connectedUser,
        setConnectedUser] =
        useState("")

    if (!connectedUser) {

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
                        onClick={() =>
                            setConnectedUser(
                                username
                            )
                        }
                    >
                        Connect
                    </Button>

                </div>

            </div>
        )
    }

    return (

        <StompProvider
            username={connectedUser}
        >

            <div className="m-5">

                <ChatApp
                    connectedUser={
                        connectedUser
                    }
                />

            </div>

        </StompProvider>
    )
}
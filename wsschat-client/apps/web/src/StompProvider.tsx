import {
    Client,
    type IMessage,
    type StompSubscription
} from "@stomp/stompjs"

import {
    createContext,
    useCallback,
    useContext,
    useEffect,
    useRef,
    useState
} from "react"


type StompContextType = {
    connected: boolean

    subscribe: (
        destination: string,
        callback: (
            message: IMessage
        ) => void
    ) => void

    unsubscribe: (
        destination: string
    ) => void

    send: (
        destination: string,
        body: unknown
    ) => void
}

export const StompContext =
    createContext<
        StompContextType | undefined
    >(undefined)

type Props = {
    username: string,
    children: React.ReactNode
}

export default function StompProvider({
    username,
    children
}: Props) {

    const clientRef =
        useRef<Client | null>(null)

    const subscriptionsRef =
        useRef<
            Map<string, StompSubscription>
        >(new Map())

    const [connected, setConnected] =
        useState(false)

    useEffect(() => {

        const client = new Client({

            brokerURL:
                "ws://localhost:8080/hello",

            // reconnectDelay: 5000,

            heartbeatIncoming: 4000,

            heartbeatOutgoing: 4000,

            connectHeaders: {
                username
            },

            onConnect: () => {

                console.log(
                    "STOMP CONNECTED"
                )

                setConnected(true)
            },

            onDisconnect: () => {

                console.log(
                    "STOMP DISCONNECTED"
                )

                setConnected(false)
            },

            onStompError: (frame) => {

                console.error(
                    frame.headers["message"]
                )

                console.error(
                    frame.body
                )
            }
        })

        client.activate()

        clientRef.current = client

        return () => {

            subscriptionsRef.current
                .forEach((sub) => {
                    sub.unsubscribe()
                })

            subscriptionsRef.current
                .clear()

            client.deactivate()
        }

    }, [username])

    const subscribe = useCallback(

        (
            destination: string,
            callback: (
                message: IMessage
            ) => void
        ) => {

            const client =
                clientRef.current

            if (
                !client ||
                !client.connected
            ) {
                return
            }

            if (
                subscriptionsRef.current.has(
                    destination
                )
            ) {
                return
            }

            const subscription =
                client.subscribe(
                    destination,
                    callback
                )

            subscriptionsRef.current.set(
                destination,
                subscription
            )
        },

        []
    )

    const unsubscribe =
        useCallback(

            (
                destination: string
            ) => {

                const sub =
                    subscriptionsRef.current.get(
                        destination
                    )

                if (!sub) return

                sub.unsubscribe()

                subscriptionsRef.current.delete(
                    destination
                )
            },

            []
        )

    const send = useCallback(

        (
            destination: string,
            body: unknown
        ) => {

            const client =
                clientRef.current

            if (
                !client ||
                !client.connected
            ) {
                return
            }

            client.publish({

                destination,

                body:
                    typeof body ===
                    "string"
                        ? body
                        : JSON.stringify(
                              body
                          )
            })
        },

        []
    )

    return (

        <StompContext.Provider
            value={{
                connected,
                subscribe,
                unsubscribe,
                send
            }}
        >

            {children}

        </StompContext.Provider>
    )
}
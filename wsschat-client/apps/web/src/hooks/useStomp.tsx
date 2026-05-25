import { useContext } from "react"
import { StompContext } from "@/StompProvider"

export function useStomp() {

    const context =
        useContext(StompContext)

    if (!context) {

        throw new Error(
            "useStomp must be used inside StompProvider"
        )
    }

    return context
}
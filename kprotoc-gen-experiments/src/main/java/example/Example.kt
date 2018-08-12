package example

import com.example.kotlin.api.v1.Address
import com.example.kotlin.api.v1.HelloworldRequest
import com.example.kotlin.api.v1.addresses

fun main(args: Array<String>) {
    val request = HelloworldRequest {
        name = "Ray"

        address = Address {
            street = "84 ABC"
            city = "New York"
        }

        addresses = listOf(
                Address {
                    city = "Irvine"
                },
                Address {
                    city = "Los Angeles"
                }
        )

        putAllLocations(mapOf(
                "chicago" to Address {
                    city = "Chicago"
                }
        ))

    }

    println(request)
}
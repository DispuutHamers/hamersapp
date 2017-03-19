package nl.ecci.hamers.stickers

internal class Sticker(
        val id: Int,
        val lat: Float,
        val lon: Float,
        val notes: String) {
    companion object {
        val ID = "ID"
    }
}

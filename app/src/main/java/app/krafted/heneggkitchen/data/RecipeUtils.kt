package app.krafted.heneggkitchen.data

fun scaleAmount(baseAmount: Double, baseServings: Int, targetServings: Int): Double {
    return baseAmount * targetServings / baseServings
}

fun formatAmount(amount: Double): String {
    return when {
        amount == amount.toLong().toDouble() -> amount.toLong().toString()
        else -> String.format("%.1f", amount)
    }
}

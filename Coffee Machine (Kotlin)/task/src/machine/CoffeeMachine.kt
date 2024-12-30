package machine

class CoffeeMachine {
    private var water = 400
    private var milk = 540
    private var beans = 120
    private var cup = 9
    private var money = 550
    private var currentAction: Action = Action.NONE


    fun run() {
       while (currentAction != Action.EXIT) {
           askAction()
           println()
           makeAction()
           println()
       }
    }

    private fun askAction() {
        do {
            println("Write action (buy, fill, take, remaining, exit):")
            currentAction = when (readln().lowercase()) {
                "buy" -> Action.BUY
                "fill" -> Action.FILL
                "take" -> Action.TAKE
                "remaining" -> Action.REMAINDER
                "exit" -> Action.EXIT
                else -> {
                    println("Invalid action")
                    Action.NONE
                }
            }
        } while (currentAction == Action.NONE)
    }

    private fun makeAction() {
        when(currentAction) {
            Action.BUY -> makeCoffee()
            Action.FILL -> refillMachine()
            Action.TAKE -> takeMoney()
            Action.REMAINDER -> displayState()
            Action.EXIT ->  return
            Action.NONE -> println("--Error--")
        }
    }

    private fun makeCoffee() {
        println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:")
        var order: Coffee?

        do {
            //Don't trust the user to input the number instead of the name of the coffee
            order = when (readln().lowercase()) {
                "1", "espresso" -> Coffee.ESPRESSO
                "2", "latte" -> Coffee.LATTE
                "3", "cappuccino" -> Coffee.CAPPUCCINO
                "back" -> return
                else -> {
                    println("Invalid coffee")
                    null
                }
            }
        } while (order == null)

        prepareCoffee(order)
    }

    private fun refillMachine() {
        water = refillIngredients("ml of water", water)
        milk = refillIngredients("ml of milk", milk)
        beans = refillIngredients("grams of coffee beans", beans)
        cup = refillIngredients("disposable cups", cup)
    }

    private fun takeMoney() {
        println("I gave you $%d".format(money))
        money = 0
    }

    private fun prepareCoffee(order: Coffee) {
        notEnoughOf(order).let {
            if (it.isEmpty()) {
                water -= order.water
                milk -= order.milk
                beans -= order.beans
                money += order.price
                cup--
                println("I have enough resources, making you a coffee!")
            } else {
                println("Sorry, not enough %s!".format(
                    it.joinToString("") { item ->
                        when (item) {
                            it.first() -> item
                            it.last() -> " and $item"
                            else -> ", $item"
                        }
                    }
                ))
            }
        }
    }

    private fun refillIngredients(ingredients:String, currentQty:Int):Int {
        println("Write how many %s you want to add:".format(ingredients))
        return currentQty + (readln().toIntOrNull() ?: 0)
    }

    private fun displayState() {
        StringBuilder().apply {
            appendLine("The coffee machine has:")
            appendLine("%d ml of water".format(water))
            appendLine("%d ml of milk".format(milk))
            appendLine("%d g of coffee beans".format(beans))
            appendLine("%d disposable cups".format(cup))
            append("$%d of money".format(money))
        }.also(::println)
    }

    private fun notEnoughOf(order: Coffee) = order.let { o ->
        listOfNotNull(
            hasEnough("water", water, o.water),
            hasEnough("milk", milk, o.milk),
            hasEnough("coffee beans", beans, o.beans),
            hasEnough("cup", cup)
        )
    }

    private fun hasEnough(ingredients: String, machineQty: Int, orderQty: Int = 1): String? {
        return if (machineQty < orderQty) ingredients else null
    }

    private enum class Action { BUY, FILL, TAKE, EXIT, REMAINDER, NONE }

    private enum class Coffee(val water:Int, val milk: Int, val beans:Int, val price: Int) {
        ESPRESSO(250, 0, 16, 4),
        LATTE(350, 75, 20, 7),
        CAPPUCCINO(200, 100, 12, 6)
    }
}
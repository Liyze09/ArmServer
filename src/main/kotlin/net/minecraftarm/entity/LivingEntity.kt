package net.minecraftarm.entity

abstract class LivingEntity : Entity() {
    var currentHealth = 20.0F
    var maxHealth = 20.0F
    fun decreaseHealth(amount: Float): LivingEntity {
        currentHealth -= amount
        if (currentHealth > maxHealth) currentHealth = maxHealth
        else if (currentHealth <= 0) {
            whenDead()
            currentDimension.entities.remove(this.entityId)
        }
        return this
    }

    abstract fun whenDead()
}
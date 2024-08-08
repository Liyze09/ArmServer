package io.github.liyze09.arms.entity

abstract class LivingEntity : Entity() {
    var currentHealth = 20.0F
    var maxHealth = 20.0F
    fun damage(amount: Float): LivingEntity {
        maxHealth -= amount
        if (maxHealth <= 0) {
            whenDead()
            currentDimension.entityMap.remove(this.position)
        }
        return this
    }

    abstract fun whenDead()
}
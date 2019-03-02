/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu.
 */

package uk.co.holborn.carparkclient;

/**
 * The Sprites class contains all the sprites used throughout the application runtime
 *
 * @author Vlad Alboiu
 * @version 1.0.3
 */
public enum Sprites {
    /**
     * Ticket insert animation
     */
    TICKET_INSERT(new SpriteSettings("/img/sprites/ticket_insert_sprite.png", 4096, 9, 25)),
    /**
     * Smart card animation
     */
    SMARTCARD_CHECK(new SpriteSettings("/img/sprites/smartcard_check_sprite.png", 4096, 10, 99, 25)),
    /**
     * ApplePay payment animation
     */
    PAYMENT_APPLE_PAY(new SpriteSettings("/img/sprites/apple_pay_contactless_sprite.png", 4096, 10, 25)),
    /**
     * Contactless card animation
     */
    PAYMENT_CARD_CONTACTLESS(new SpriteSettings("/img/sprites/card_pay_anim_sprite.png", 4096, 10, 99, 25)),
    /**
     * Classic card payment
     */
    PAYMENT_CARD_CLASSIC(new SpriteSettings("/img/sprites/classic_card_pay_sprite.png", 4096, 9, 25)),
    /**
     * Thank you text animation
     */
    THANK_YOU(new SpriteSettings("/img/sprites/thank_you_sprite.png", 4096, 6, 25)),
    /**
     *Inserting coins
     */
    COINS_IN(new SpriteSettings("/img/sprites/coins_in_sprite.png", 4096, 9, 81, 1, 0, 25));

    private final SpriteSettings spriteSettings;

    /**
     * Constructor that assigns SpriteSettings for each Sprite
     *
     * @param spriteSettings the settings to update the sprite with
     */
    Sprites(SpriteSettings spriteSettings) {
        this.spriteSettings = spriteSettings;
    }

    /**
     * Methd that gets the sprite settings from a Sprite
     *
     * @return sprite settings
     */
    public SpriteSettings getSpriteSettings() {
        return spriteSettings;
    }
}

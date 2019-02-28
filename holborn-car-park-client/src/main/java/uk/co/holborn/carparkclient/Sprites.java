package uk.co.holborn.carparkclient;

/**
 * The Sprites class contains all the sprites used throughout the application runtime
 *
 * @author Vlad Alboiu
 * @version 1.0.3
 */
public enum Sprites {
    TICKET_INSERT(new SpriteSettings("/img/sprites/ticket_insert_sprite.png", 4096, 9, 25)),
    SMARTCARD_CHECK(new SpriteSettings("/img/sprites/smartcard_check_sprite.png", 4096, 10,99, 25)),
    PAYMENT_APPLE_PAY(new SpriteSettings("/img/sprites/apple_pay_contactless_sprite.png", 4096, 10, 25)),
    PAYMENT_CARD_CONTACTLESS(new SpriteSettings("/img/sprites/card_pay_anim_sprite.png", 4096, 10, 99, 25)),
    THANK_YOU(new SpriteSettings("/img/sprites/thank_you_sprite.png", 4096, 6, 25));

    private SpriteSettings spriteSettings;

    /**
     * Constructor that assigns SpriteSettings for each Sprite
     *
     * @param spriteSettings
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

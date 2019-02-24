package uk.co.holborn.carparkclient;

/**
 * The Sprites class contains all the sprites used throughout the application runtime
 * <p></p>
 *
 * @author Vlad Alboiu
 * @version 1.0.1
 */
public enum Sprites {
    TICKET_INSERT(new SpriteSettings("/img/ticket_insert_sprite.png", 4096, 9, 25)),
    PAYMENT_APPLE_PAY(new SpriteSettings("/img/apple_pay_contactless_sprite.png", 4096, 10, 25)),
    PAYMENT_CARD_CONTACTLESS(new SpriteSettings("/img/card_pay_anim_sprite.png", 4096, 10, 99, 25));;

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

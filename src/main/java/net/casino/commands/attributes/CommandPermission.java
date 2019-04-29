package net.casino.commands.attributes;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermission {

    /**
     * Get the permission required to run this command.
     * @return Permission
     */
    String permission();
}

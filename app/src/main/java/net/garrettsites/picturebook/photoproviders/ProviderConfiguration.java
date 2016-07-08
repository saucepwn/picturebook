package net.garrettsites.picturebook.photoproviders;

/**
 * Created by Garrett on 6/28/2016.
 */
public interface ProviderConfiguration {
    /**
     * @return A resource that resolves to the photo provider's name. Ex) A string that resolves to
     * "Facebook".
     */
    int getNameResource();

    /**
     * @return A color resource that represents the primary color of this photo provider. The color
     * is used in UI elements.
     */
    int getColorResource();

    /**
     * @return A drawable resource representing the provider's icon.
     */
    int getIconResource();

    /**
     * @return A two-character code that represents this provider.
     */
    String getShortName();

    /**
     * Each provider has its own Activity used to add or remove user accounts. This activity is
     * launched when the user clicks the "link account" or "unlink account" button in the Manage
     * Accounts Fragment.
     * @return The provider's manage accounts activity.
     */
    Class<?> getManageAccountsActivity();
}

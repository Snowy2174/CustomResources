package plugin.customresources.objects;

/**
 * This class represents a quantity of a resource
 */
public class ResourceQuantity {
    private final String resource;
    private final int quantity;

     public ResourceQuantity(String resource, int quantity) {
        this.resource = resource;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getResource() {
        return resource;
    }
}

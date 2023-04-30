package plugin.customresources.objects;

public class Resource {

    private final String category;              //Category of the resource
    private final String material;              //Name of the resource material
    private final int baseAmount;               //Base amount of the resource

    public Resource(String category, String material, int baseAmount, int discoveryProbabilityWeight, int discoveryId) {
        this.category = category;
        this.material = material;
        this.baseAmount = baseAmount;
    }

    public String getCategory() {
        return category;
    }

    public String getMaterial() {
        return material;
    }

    public int getBaseAmount() {
        return baseAmount;
    }
}

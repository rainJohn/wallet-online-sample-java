package com.google.pvacameras.server.multi;

/**
 * Item represents an item available for purchase on the XYZ cameras test merchant site.
 */
public class Item {

  public static final int TAX = 8;
  public static final double SHIPPING = 9.99;
  private String description;
  private String description2;
  private String imageUrl;
  private int price;
  private String name;
  private String id;

  public static final Item CAMERA_1 = new Item("1", "Camera XY000", 800, "img/XY000-small.png",
      "High Resolution 16.2 MP DX-format CMOS sensor Body only.", "Lenses sold separately. " +
      "High Speed 6 frames per second continuous shooting up to 100 shots.");
  public static final Item CAMERA_2 = new Item("2", "Camera XY001", 1950, "img/XY001-small.png",
      "High Resolution 16.2 MP DX-format CMOS sensor Body only.", "Lenses sold separately. " +
      "High Speed 6 frames per second continuous shooting up to 100 shots.");
  public static final Item CAMERA_3 = new Item("3", "Camera XY002", 2100, "img/XY002-small.png",
      "High Resolution 16.2 MP DX-format CMOS sensor Body only.", "Lenses sold separately. " +
      "High Speed 6 frames per second continuous shooting up to 100 shots.");

  public Item(String id,
      String name,
      int price,
      String imageUrl,
      String description,
      String description2) {
    setId(id);
    setName(name);
    setPrice(price);
    setImageUrl(imageUrl);
    setDescription(description);
    setDescription2(description2);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription2(String description2) {
    this.description2 = description2;
  }

  public String getDescription2() {
    return description2;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  /**
   * returns an Item, CAMERA_1 is the default if itemId doesn't match anything.
   */
  public static Item getForId(String itemId) {
    if ("2".equals(itemId)) {
      return CAMERA_2;
    }
    if ("3".equals(itemId)) {
      return CAMERA_3;
    }
    return CAMERA_1;
  }
}

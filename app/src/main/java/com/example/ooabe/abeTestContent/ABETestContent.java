package com.example.ooabe.abeTestContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample/default content for user interfaces created by
 * Android template wizards.
 * Modified from the template
 * Contain all the item names listed in MainActivity
 */
public class ABETestContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();
    public static final Map<String, Integer> ActionNumber = new HashMap<String, Integer>();

    public static final String introText =
    "Welcome to the test GUI of online-offline attribute-based encryption schemes.\n" +
            "To start the test, such as encryption and decryption, you may first need to go into \"Generate PP and PK\" in the start page.\n" +
            "If you're using a smart phone you may need to hit the back button to return to the start page.\n" +
            "Have fun :)";


    public static DummyItem GeneratePK = new DummyItem("1", "Generate PP and PK","");
    public static DummyItem EncTest = new DummyItem("2", "Test Encryption","");
    public static DummyItem DecTest = new DummyItem("3", "Test uskGen and Decryption","");
    public static DummyItem Introduction = new DummyItem("4", "Introduction",introText);

    static
    {
        addItem(GeneratePK);
        addItem(EncTest);
        addItem(DecTest);
        addItem(Introduction);
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }


    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}

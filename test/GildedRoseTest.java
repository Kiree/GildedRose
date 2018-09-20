import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import fi.oulu.tol.sqat.GildedRose;
import fi.oulu.tol.sqat.Item;

public class GildedRoseTest {

	private GildedRose store;

	@Before
	public void initializeStore() {
		store = new GildedRose();
	}

	@Test
	public void testUpdateEndOfDay_AgedBrie_Quality_10_11() {
		// Arrange
		store.addItem(new Item("Aged Brie", 2, 10) );
		// Act
		GildedRose.updateEndOfDay();
		// Assert
		List<Item> items = store.getItems();
		Item itemBrie = items.get(0);
		assertEquals("Quality should be 11.", 11, itemBrie.getQuality());
	}

	@Test
	public void testUpdateEndOfDay_DexVest_QualityDecrease() {
		//Arrange
		store.addItem(new Item("+5 Dexterity Vest", 10, 20));
		//Act
		GildedRose.updateEndOfDay();
		//Assert
		List<Item> items = store.getItems();
		Optional<Item> dexVest = items.stream().findFirst();
		dexVest.ifPresent(thisVest ->
				assertEquals("Quality should be 19.", 19, thisVest.getQuality())
		);
	}

	@Test
	public void testUpdateEndOfDay_DexVest_SellInDecrease() {
		//Arrange
		store.addItem(new Item("+5 Dexterity Vest", 10, 20));
		//Act
		GildedRose.updateEndOfDay();
		//Assert
		List<Item> items = store.getItems();
		Optional<Item> dexVest = items.stream().findFirst();
		dexVest.ifPresent(thisVest ->
				assertEquals("SellIn should be 9.", 9, thisVest.getSellIn())
		);
	}

	@Test
	public void testUpdateEndOfDay_DexVest_SellInGoesIntoNegatives() {
		//Arrange
		store.addItem(new Item("+5 Dexterity Vest", 0, 10));
		//Act
		GildedRose.updateEndOfDay();
		//Assert
		List<Item> items = store.getItems();
		Optional<Item> dexVest = items.stream().findFirst();
		dexVest.ifPresent(thisVest ->
				assertEquals("SellIn should be -1.", -1, thisVest.getSellIn())
		);
	}

	@Test
	public void testUpdateEndOfDay_DexVest_QualityDropDoubles() {
		//Arrange
		store.addItem(new Item("+5 Dexterity Vest", 0, 10));
		int initialQuality = 10;
		//Act
		GildedRose.updateEndOfDay();
		//Assert
		List<Item> items = store.getItems();
		Optional<Item> dexVest = items.stream().findFirst();
		dexVest.ifPresent(thisVest ->
				assertEquals("Quality should be 2 less than before update.", 2, initialQuality - thisVest.getQuality())
		);
	}

	@Test
	public void testUpdateEndOfDay_Elixir_QualityDoesNotDropBelow0() {
		//Arrange
		store.addItem(new Item("Elixir of the Mongoose", 5, 7));
		//Act
		GildedRose.updateEndOfDay();
		GildedRose.updateEndOfDay();
		GildedRose.updateEndOfDay();
		GildedRose.updateEndOfDay();
		GildedRose.updateEndOfDay(); // SellIn should be 0, Quality 2
		GildedRose.updateEndOfDay(); // SellIn should be -1, Quality 0
		GildedRose.updateEndOfDay(); // SellIn should be -2, Quality 0
		//Assert
		List<Item> items = store.getItems();
		Optional<Item> elixir = items.stream().findFirst();
		elixir.ifPresent(thisVest ->
				assertEquals("Quality should not be below 0", 0, thisVest.getQuality())
		);
	}

	@Test
	public void testUpdateEndOfDay_AgedBrie_QualityIncreaseDoubles() {
		//Arrange
		store.addItem(new Item("Aged Brie", 2, 0));
		//Act
		GildedRose.updateEndOfDay();
		GildedRose.updateEndOfDay(); //SellIn should be 0, quality 2
		GildedRose.updateEndOfDay(); //SellIn should be -1, quality 4
		//Assert
		List<Item> items = store.getItems();
		Optional<Item> brie = items.stream().findFirst();
		brie.ifPresent(thisBrie ->
				assertEquals("Quality should be 4.", 4, thisBrie.getQuality())
		);
	}

	@Test
	public void testUpdateEndOfDay_AgedBrie_QualityDoesNotExceed50() {
		//Arrange
		store.addItem(new Item("Aged Brie", -1, 50));
		//Act
		GildedRose.updateEndOfDay(); //SellIn should be -2, Quality 50
		//Assert
		List<Item> items = store.getItems();
		Optional<Item> brie = items.stream().findFirst();
		brie.ifPresent(thisBrie ->
				assertEquals("Quality should be 50.", 50, thisBrie.getQuality())
		);
	}

	@Test
	public void testUpdateEndOfDay_Sulfuras_QualityShouldRemainSame() {
		//Assert
		store.addItem(new Item("Sulfuras, Hand of Ragnaros", 0, 80));
		int initialQuality = 80;
		//Act
		GildedRose.updateEndOfDay();
		//Assert
		List<Item> items = store.getItems();
		Optional<Item> sulfuras = items.stream().findFirst();
		sulfuras.ifPresent(thisSulfuras ->
			assertEquals("Quality should not have changed.", initialQuality, thisSulfuras.getQuality())
		);
	}

	@Test
	public void testUpdateEndOfDay_Sulfuras_SellInShouldRemainSame() {
		//Assert
		store.addItem(new Item("Sulfuras, Hand of Ragnaros", 0, 80));
		int initialSellIn = 0;
		//Act
		GildedRose.updateEndOfDay();
		//Assert
		List<Item> items = store.getItems();
		Optional<Item> sulfuras = items.stream().findFirst();
		sulfuras.ifPresent(thisSulfuras ->
				assertEquals("SellIn should not have changed.", initialSellIn, thisSulfuras.getSellIn())
		);
	}

	@Test
	public void testUpdateEndOfDay_ETCPass_QualityIncreasesBy1() {
		//Assert
		store.addItem(new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20));
		int initialQuality = 20;
		//Act
		GildedRose.updateEndOfDay();
		//Assert
		List<Item> items = store.getItems();
		Optional<Item> etcPass = items.stream().findFirst();
		etcPass.ifPresent(thisETCPass ->
				assertEquals("Quality should have increased by 1.", -1, initialQuality - thisETCPass.getQuality())
		);
	}

	@Test
	public void testUpdateEndOfDay_ETCPass_QualityIncreasesBy2() {
		//Assert
		store.addItem(new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20));
		int initialQuality = 20;
		//Act
		GildedRose.updateEndOfDay();
		//Assert
		List<Item> items = store.getItems();
		Optional<Item> etcPass = items.stream().findFirst();
		etcPass.ifPresent(thisETCPass ->
				assertEquals("Quality should have increased by 2.", -2, initialQuality - thisETCPass.getQuality())
		);
	}

	@Test
	public void testUpdateEndOfDay_ETCPass_QualityIncreasesBy3() {
		//Assert
		store.addItem(new Item("Backstage passes to a TAFKAL80ETC concert", 5, 20));
		int initialQuality = 20;
		//Act
		GildedRose.updateEndOfDay();
		//Assert
		List<Item> items = store.getItems();
		Optional<Item> etcPass = items.stream().findFirst();
		etcPass.ifPresent(thisETCPass ->
				assertEquals("Quality should have increased by 3.", -3, initialQuality - thisETCPass.getQuality())
		);
	}

	@Test
	public void testUpdateEndOfDay_ETCPass_QualityDropsTo0() {
		//Assert
		store.addItem(new Item("Backstage passes to a TAFKAL80ETC concert", 0, 20));
		//Act
		GildedRose.updateEndOfDay();
		//Assert
		List<Item> items = store.getItems();
		Optional<Item> etcPass = items.stream().findFirst();
		etcPass.ifPresent(thisETCPass ->
				assertEquals("Quality should be 0.", 0, thisETCPass.getQuality())
		);
	}
}

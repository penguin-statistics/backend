package io.penguinstats.bean;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

public class Limitation extends Documentable {

	private String name; // 'name' can be 'all', a stageId, or any identifier.
	private Bounds itemTypeBounds;
	private List<ItemQuantityBounds> itemQuantityBounds;
	private List<String> inheritance;

	public Limitation() {}

	public Limitation(String name, Bounds itemTypeBounds, List<ItemQuantityBounds> itemQuantityBounds,
			List<String> inheritance) {
		this.name = name;
		this.itemTypeBounds = itemTypeBounds;
		this.itemQuantityBounds = itemQuantityBounds;
		this.inheritance = inheritance;
	}

	@SuppressWarnings("unchecked")
	public Limitation(Document doc) {
		this.name = doc.getString("name");
		this.itemTypeBounds = new Bounds((Document)doc.get("itemTypeBounds"));
		List<Document> itemQuantityBoundsDocList = (ArrayList<Document>)doc.get("itemQuantityBounds");
		this.itemQuantityBounds = new ArrayList<>();
		itemQuantityBoundsDocList.forEach(
				itemQuantityBoundsDoc -> this.itemQuantityBounds.add(new ItemQuantityBounds(itemQuantityBoundsDoc)));
		this.inheritance = (ArrayList<String>)doc.get("inheritance");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Bounds getItemTypeBounds() {
		return itemTypeBounds;
	}

	public void setItemTypeBounds(Bounds itemTypeBounds) {
		this.itemTypeBounds = itemTypeBounds;
	}

	public List<ItemQuantityBounds> getItemQuantityBounds() {
		return itemQuantityBounds;
	}

	public void setItemQuantityBounds(List<ItemQuantityBounds> itemQuantityBounds) {
		this.itemQuantityBounds = itemQuantityBounds;
	}

	public List<String> getInheritance() {
		return inheritance;
	}

	public void setInheritance(List<String> inheritance) {
		this.inheritance = inheritance;
	}

	@Override
	public Document toDocument() {
		List<Document> itemQuantityBoundsDocs = new ArrayList<>();
		for (ItemQuantityBounds itemQuantityBounds : this.itemQuantityBounds) {
			itemQuantityBoundsDocs.add(itemQuantityBounds.toDocument());
		}
		return new Document().append("name", this.name).append("itemTypeBounds", this.itemTypeBounds.toDocument())
				.append("itemQuantityBounds", itemQuantityBoundsDocs).append("inheritance", this.inheritance);
	}

	public JSONObject asJSON() {
		JSONArray itemQuantityBoundsArray = new JSONArray();
		for (ItemQuantityBounds itemQuantityBounds : this.itemQuantityBounds) {
			itemQuantityBoundsArray.put(itemQuantityBounds.asJSON());
		}
		return new JSONObject().put("name", this.name).put("itemTypeBounds", this.itemTypeBounds.asJSON())
				.put("itemQuantityBounds", itemQuantityBoundsArray).put("inheritance", this.inheritance);
	}

	public static class Bounds extends Documentable {

		private Integer lower;
		private Integer upper;
		private List<Integer> exceptions;

		public Bounds() {
			this.lower = null;
			this.upper = null;
			this.exceptions = new ArrayList<>();
		}

		public Bounds(Integer lower, Integer upper, List<Integer> exceptions) {
			this.lower = lower;
			this.upper = upper;
			this.exceptions = exceptions;
		}

		public Bounds(Integer lower, Integer upper) {
			this.lower = lower;
			this.upper = upper;
			this.exceptions = new ArrayList<>();
		}

		@SuppressWarnings("unchecked")
		public Bounds(Document doc) {
			this.lower = doc.getInteger("lower");
			this.upper = doc.getInteger("upper");
			this.exceptions = (ArrayList<Integer>)doc.get("exceptions");

		}

		public Integer getLower() {
			return lower;
		}

		public void setLower(Integer lower) {
			this.lower = lower;
		}

		public Integer getUpper() {
			return upper;
		}

		public void setUpper(Integer upper) {
			this.upper = upper;
		}

		public List<Integer> getExceptions() {
			return exceptions;
		}

		public void setExceptions(List<Integer> exceptions) {
			this.exceptions = exceptions;
		}

		@Override
		public Document toDocument() {
			return new Document().append("lower", this.lower).append("upper", this.upper).append("exceptions",
					this.exceptions);
		}

		public JSONObject asJSON() {
			return new JSONObject().put("lower", this.lower).put("upper", this.upper).put("exceptions",
					this.exceptions);
		}

	}

	public static class ItemQuantityBounds extends Documentable {

		private String itemId;
		private Bounds bounds;

		public ItemQuantityBounds() {}

		public ItemQuantityBounds(String itemId, Bounds bounds) {
			this.itemId = itemId;
			this.bounds = bounds;
		}

		public ItemQuantityBounds(Document doc) {
			this.itemId = doc.getString("itemId");
			this.bounds = new Bounds((Document)doc.get("bounds"));
		}

		public String getItemId() {
			return itemId;
		}

		public void setItemId(String itemId) {
			this.itemId = itemId;
		}

		public Bounds getBounds() {
			return bounds;
		}

		public void setBounds(Bounds bounds) {
			this.bounds = bounds;
		}

		@Override
		public Document toDocument() {
			return new Document().append("itemId", this.itemId).append("bounds", this.bounds.toDocument());
		}

		public JSONObject asJSON() {
			return new JSONObject().put("itemId", this.itemId).put("bounds", this.bounds.asJSON());
		}

	}

}

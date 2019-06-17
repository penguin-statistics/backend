package io.penguinstats.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import io.penguinstats.bean.DropMatrix;
import io.penguinstats.bean.ItemDrop;
import io.penguinstats.service.DropMatrixService;
import io.penguinstats.service.ItemDropService;

public class Scripts {

	private static final DropMatrixService dropMatrixService = DropMatrixService.getInstance();
	private static final ItemDropService itemDropService = ItemDropService.getInstance();

	public static void main(String[] args) {
		outputAllReliableItemDrops();
	}

	private static void clearAndUpdateDropMatrix() {
		List<DropMatrix> elements = itemDropService.generateDropMatrixList();
		dropMatrixService.clearAndUpdateAll(elements);
	}

	private static void outputAllReliableItemDrops() {
		List<ItemDrop> itemDrops = itemDropService.getAllReliableItemDrops();
		JSONArray array = new JSONArray();
		for (ItemDrop itemDrop : itemDrops) {
			array.put(itemDrop.asJSON());
		}
		JSONObject obj = new JSONObject().put("itemDrops", array);
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter("itemDrops.json"));
			writer.write(obj.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

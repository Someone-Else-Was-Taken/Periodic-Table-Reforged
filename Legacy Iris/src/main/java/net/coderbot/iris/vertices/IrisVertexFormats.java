package net.coderbot.iris.vertices;

import com.google.common.collect.ImmutableList;
//import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//import com.mojang.blaze3d.vertex.VertexFormat;
//import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class IrisVertexFormats {
	public static final VertexFormatElement ENTITY_ELEMENT;
	public static final VertexFormatElement MID_TEXTURE_ELEMENT;
	public static final VertexFormatElement TANGENT_ELEMENT;

	public static final VertexFormat TERRAIN;

	static {
		ENTITY_ELEMENT = new VertexFormatElement(10, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 4);
		MID_TEXTURE_ELEMENT = new VertexFormatElement(11, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 2);
		TANGENT_ELEMENT = new VertexFormatElement(12, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 4);

		ImmutableList.Builder<VertexFormatElement> elements = ImmutableList.builder();

		elements.add(DefaultVertexFormats.ELEMENT_POSITION);
		elements.add(DefaultVertexFormats.ELEMENT_COLOR);
		elements.add(DefaultVertexFormats.ELEMENT_UV0);
		elements.add(DefaultVertexFormats.ELEMENT_UV2);
		elements.add(DefaultVertexFormats.ELEMENT_NORMAL);
		elements.add(DefaultVertexFormats.ELEMENT_PADDING);
		elements.add(ENTITY_ELEMENT);
		elements.add(MID_TEXTURE_ELEMENT);
		elements.add(TANGENT_ELEMENT);

		TERRAIN = new VertexFormat(elements.build());
	}
}

package com.fss.pos.base.commons;

import java.util.HashMap;
import java.util.Map;

/**
 * Emv tags
 * 
 * @author Priyan
 *
 */
public enum EmvTags {

	TAG_9F01("9F01"), TAG_9F40("9F40"), TAG_81("81"), TAG_9F02("9F02"), TAG_9F04(
			"9F04"), TAG_9F03("9F03"), TAG_9F3A("9F3A"), TAG_9F26("9F26"), TAG_9F42(
			"9F42"), TAG_9F44("9F44"), TAG_9F05("9F05"), TAG_5F25("5F25"), TAG_5F24(
			"5F24"), TAG_94("94"), TAG_4F("4F"), TAG_9F06("9F06"), TAG_82("82"), TAG_50(
			"50"), TAG_9F12("9F12"), TAG_5A("5A"), TAG_5F34("5F34"), TAG_87(
			"87"), TAG_9F3B("9F3B"), TAG_9F43("9F43"), TAG_61("61"), TAG_9F36(
			"9F36"), TAG_9F07("9F07"), TAG_9F08("9F08"), TAG_9F09("9F09"), TAG_89(
			"89"), TAG_8A("8A"), TAG_5F54("5F54"), TAG_8C("8C"), TAG_8D("8D"), TAG_5F20(
			"5F20"), TAG_9F0B("9F0B"), TAG_8E("8E"), TAG_9F34("9F34"), TAG_8F(
			"8F"), TAG_9F22("9F22"), TAG_83("83"), TAG_9F27("9F27"), TAG_9F45(
			"9F45"), TAG_84("84"), TAG_9D("9D"), TAG_73("73"), TAG_9F49("9F49"), TAG_70(
			"70"), TAG_BF0C("BF0C"), TAG_A5("A5"), TAG_6F("6F"), TAG_9F4C(
			"9F4C"), TAG_9F2D("9F2D"), TAG_9F2E("9F2E"), TAG_9F2F("9F2F"), TAG_9F46(
			"9F46"), TAG_9F47("9F47"), TAG_9F48("9F48"), TAG_9F1E("9F1E"), TAG_5F53(
			"5F53"), TAG_9F0D("9F0D"), TAG_9F0E("9F0E"), TAG_9F0F("9F0F"), TAG_9F10(
			"9F10"), TAG_91("91"), TAG_9F11("9F11"), TAG_5F28("5F28"), TAG_5F55(
			"5F55"), TAG_5F56("5F56"), TAG_42("42"), TAG_90("90"), TAG_9F32(
			"9F32"), TAG_92("92"), TAG_86("86"), TAG_9F18("9F18"), TAG_71("71"), TAG_72(
			"72"), TAG_5F50("5F50"), TAG_5F2D("5F2D"), TAG_9F13("9F13"), TAG_9F4D(
			"9F4D"), TAG_9F4F("9F4F"), TAG_9F14("9F14"), TAG_9F15("9F15"), TAG_9F16(
			"9F16"), TAG_9F4E("9F4E"), TAG_9F17("9F17"), TAG_9F39("9F39"), TAG_9F38(
			"9F38"), TAG_80("80"), TAG_77("77"), TAG_5F30("5F30"), TAG_88("88"), TAG_9F4B(
			"9F4B"), TAG_93("93"), TAG_9F4A("9F4A"), TAG_9F33("9F33"), TAG_9F1A(
			"9F1A"), TAG_9F1B("9F1B"), TAG_9F1C("9F1C"), TAG_9F1D("9F1D"), TAG_9F35(
			"9F35"), TAG_95("95"), TAG_9F1F("9F1F"), TAG_9F20("9F20"), TAG_57(
			"57"), TAG_98("98"), TAG_97("97"), TAG_5F2A("5F2A"), TAG_5F36(
			"5F36"), TAG_9A("9A"), TAG_99("99"), TAG_9F3C("9F3C"), TAG_9F3D(
			"9F3D"), TAG_9F41("9F41"), TAG_9B("9B"), TAG_9F21("9F21"), TAG_9C(
			"9C"), TAG_9F37("9F37"), TAG_9F23("9F23"), TAG_9F53("9F53"), TAG_5F0D(
			"5F0D"), TAG_9F6E("9F6E"), TAG_DF5B("DF5B"), TAG_5F09("5F09"), TAG_5F0A(
			"5F0A"), TAG_5F0B("5F0B"), TAG_5F0C("5F0C"), TAG_9F5B("9F5B"), TAG_9F7C("9F7C"), TAG_DF15("DF15"), TAG_DF7C("DF7C"), TAG_9F24("9F24"),
			TAG_9F63("9F63");
	;

	private static final Map<String, EmvTags> m;

	static {
		m = new HashMap<String, EmvTags>();
		for (EmvTags v : EmvTags.values())
			m.put(v.tag, v);
	}

	public static EmvTags getEmvTag(String tag) {
		return m.get(tag);
	}

	private String tag;

	private EmvTags(String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return tag;
	}

}

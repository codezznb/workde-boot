package cn.workde.core.builder.utils;

import java.io.File;
import java.text.CollationKey;
import java.text.Collator;
import java.util.*;

/**
 * @author zhujingang
 * @date 2019/9/18 1:48 PM
 */
public class SortUtil {

	public static File[] sort(final File[] files) {
		sort(files, 0, false);
		return files;
	}

	public static File[] sort(final File[] files, final int type, final boolean desc) {
		final int fType = type;
		final boolean fDesc = desc;
		final Collator collator = Collator.getInstance();
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(final File f1, final File f2) {
				switch (fType) {
					case 1: {
						final Long l1 = f1.isDirectory() ? -1L : f1.length();
						final Long l2 = f2.isDirectory() ? -1L : f2.length();
						if (fDesc) {
							return l2.compareTo(l1);
						}
						return l1.compareTo(l2);
					}
					case 2: {
						final CollationKey t1 = collator.getCollationKey(f1.isDirectory() ? "0" : ("1" + FileUtil.getFileType(f1).toLowerCase()));
						final CollationKey t2 = collator.getCollationKey(f2.isDirectory() ? "0" : ("1" + FileUtil.getFileType(f2).toLowerCase()));
						if (fDesc) {
							return t2.compareTo(t1);
						}
						return t1.compareTo(t2);
					}
					case 3: {
						Long d1 = f1.lastModified();
						Long d2 = f2.lastModified();
						final boolean b1 = f1.isDirectory();
						final boolean b2 = f2.isDirectory();
						if (b1 && !b2) {
							d1 = Long.MIN_VALUE;
						}
						if (b2 && !b1) {
							d2 = Long.MIN_VALUE;
						}
						if (fDesc) {
							return d2.compareTo(d1);
						}
						return d1.compareTo(d2);
					}
					default: {
						final String file1 = f1.getName();
						final String file2 = f2.getName();
						final CollationKey k1 = collator.getCollationKey(String.valueOf(f1.isDirectory() ? 0 : 1) + FileUtil.removeExtension(file1).toLowerCase());
						final CollationKey k2 = collator.getCollationKey(String.valueOf(f2.isDirectory() ? 0 : 1) + FileUtil.removeExtension(file2).toLowerCase());
						int result;
						if (fDesc) {
							result = k2.compareTo(k1);
						}
						else {
							result = k1.compareTo(k2);
						}
						if (result == 0) {
							final CollationKey ke1 = collator.getCollationKey(String.valueOf(f1.isDirectory() ? 0 : 1) + FileUtil.getFileExt(file1).toLowerCase());
							final CollationKey ke2 = collator.getCollationKey(String.valueOf(f2.isDirectory() ? 0 : 1) + FileUtil.getFileExt(file2).toLowerCase());
							if (fDesc) {
								result = ke2.compareTo(ke1);
							}
							else {
								result = ke1.compareTo(ke2);
							}
						}
						return result;
					}
				}
			}
		});
		return files;
	}

	public static String[] sort(final String[] list) {
		Arrays.sort(list, new Comparator<String>() {
			Collator collator = Collator.getInstance();

			@Override
			public int compare(final String s1, final String s2) {
				final CollationKey key1 = this.collator.getCollationKey(StringUtil.opt(s1).toLowerCase());
				final CollationKey key2 = this.collator.getCollationKey(StringUtil.opt(s2).toLowerCase());
				return key1.compareTo(key2);
			}
		});
		return list;
	}

	public static <K, V> ArrayList<Map.Entry<K, V>> sortKey(final Map<K, V> map) {
		return sortKey(map, false);
	}

	public static <K, V> ArrayList<Map.Entry<K, V>> sortKey(final Map<K, V> map, final boolean keyAsNumber) {
		final ArrayList<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(map.entrySet());
		final boolean keyAsNum = keyAsNumber;
		final Collator collator = Collator.getInstance();
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(final Map.Entry<K, V> e1, final Map.Entry<K, V> e2) {
				final Object k1 = e1.getKey();
				final Object k2 = e2.getKey();
				if (!keyAsNum) {
					final CollationKey key1 = collator.getCollationKey(k1.toString().toLowerCase());
					final CollationKey key2 = collator.getCollationKey(k2.toString().toLowerCase());
					return key1.compareTo(key2);
				}
				if (k1 instanceof Number && k2 instanceof Number) {
					return (int)Math.ceil(((Number)k1).doubleValue() - ((Number)k2).doubleValue());
				}
				return (int)Math.ceil(Double.parseDouble(k1.toString()) - Double.parseDouble(k2.toString()));
			}
		});
		return list;
	}

	public static <K, V> ArrayList<Map.Entry<K, V>> sortValue(final Map<K, V> map) {
		return sortValue(map, false);
	}

	public static <K, V> ArrayList<Map.Entry<K, V>> sortValue(final Map<K, V> map, final boolean keyAsNumber) {
		final ArrayList<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(map.entrySet());
		final boolean keyAsNum = keyAsNumber;
		final Collator collator = Collator.getInstance();
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(final Map.Entry<K, V> e1, final Map.Entry<K, V> e2) {
				final Object v1 = e1.getValue();
				final Object v2 = e2.getValue();
				if (!keyAsNum) {
					final CollationKey key1 = collator.getCollationKey(v1.toString().toLowerCase());
					final CollationKey key2 = collator.getCollationKey(v2.toString().toLowerCase());
					return key1.compareTo(key2);
				}
				if (v1 instanceof Number && v2 instanceof Number) {
					return (int)Math.ceil(((Number)v1).doubleValue() - ((Number)v2).doubleValue());
				}
				return (int)Math.ceil(Double.parseDouble(v1.toString()) - Double.parseDouble(v2.toString()));
			}
		});
		return list;
	}

	public static <T> List<T> sort(final List<T> list) {
		Collections.sort(list, new Comparator<T>() {
			@Override
			public int compare(final T b1, final T b2) {
				return b1.toString().toLowerCase().compareTo(b2.toString().toLowerCase());
			}
		});
		return list;
	}
}

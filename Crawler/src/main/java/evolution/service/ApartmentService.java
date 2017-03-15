package evolution.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApartmentService {
	public List<Map<String, String>> search(String url) {
		try {
			Document document = Jsoup.connect(url).get();
			Elements as = document.select("a[href]");
			List<Map<String, String>> infos = new ArrayList<>();
			for (Element a : as) {
				Map<String, String> info = new HashMap<>();
				String href = a.attr("href");
				if (href.contains("http://") && href.contains("shtml")) {
					info.put("url", href);
					info.put("title", a.html());
					try {
						// Get the general information.
						Document subDocument = Jsoup.connect(href).get();
						info.put("content", subDocument.select("meta[name='description']").attr("content"));
						// Get the key-value-like information.
						Elements uls = subDocument.select("ul.f14");
						for (Element ul : uls) {
							Elements lis = ul.children();
							for (Element li : lis) {
								try {
									info.put(li.child(0).ownText(), li.child(1).ownText());
								} catch (Exception e) {}
							}
						}
						infos.add(briefInfo(info));
					} catch (Exception e) {}
				}
			}
			// Sort infos.
			Comparator<Map<String, String>> byPricePerSize = Comparator.comparing(
					x -> new Double(x.get("pricePerSize"))
					);
			infos = infos.stream().sorted(byPricePerSize).collect(Collectors.toList());
			return infos;
		} catch (Exception e) {
			return null;
		}
	}
	
	public Map<String, String> briefInfo(Map<String, String> info) {
		String content = info.get("content");
		int firstIndexOfPrice = content.indexOf("价格");
		int lastIndexOfPrice = content.indexOf("元");
		// Extract price.
		try {
			info.put("price", content.substring(firstIndexOfPrice + 2, lastIndexOfPrice));
		} catch (Exception e) {}
		// Extract size.
		try {
			String apartmentType = info.get("房屋类型：");
			int anciliaryIndex = apartmentType.indexOf("平");
			int firstIndexOfSize = anciliaryIndex - 5;
			int lastIndexOfSize = anciliaryIndex - 1;
			info.put("size", apartmentType.substring(firstIndexOfSize, lastIndexOfSize).replace((char) 160 + "", ""));
		} catch (Exception e) {}
		Map<String, String> briefInfo = new HashMap<>();
		briefInfo.put("price", info.get("price"));
		briefInfo.put("size", info.get("size"));
		briefInfo.put("url", info.get("url"));
		briefInfo.put("pricePerSize", new Double(info.get("price")) / new Double(info.get("size")) + "");
		log.info(briefInfo.toString());
		return briefInfo;
	}
	
	@Test
	public void test() {
		String url = "http://sz.58.com/chuzu/sub/l4000043/b11/?key=%E7%A7%9F%E6%88%BF&cmcskey=&final=1&specialtype=gls&pagetype=ditie";
		List<Map<String, String>> infos = search(url);
		System.out.println(infos);
	}
}

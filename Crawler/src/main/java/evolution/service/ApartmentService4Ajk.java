package evolution.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.stereotype.Service;

import evolution.entity.Apartment;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApartmentService4Ajk implements ApartmentService<Apartment> {
	@Override
	public List<Apartment> search(String url) {
		try {
			Elements zuItemModes = Jsoup.connect(url).get().select("div.zu-itemmod");
			List<Apartment> apartments = new LinkedList<>();
			for (Element zuItemMode : zuItemModes) {
				Map<String, String> info = new HashMap<>();
				String link = zuItemMode.attr("link");
				info.put("url", link);// Get the URL.
				try {
					Document document = Jsoup.connect(link).get();
					// Get the price.
					String price = document.select("span.f26").first().ownText();
					info.put("price", price);
					// Get the relevant information.
					Elements pPhrases = document.select("dl.p_phrase.cf");// Selects dl tags with class="p_phrase cf". 
					for (Element pPhrase : pPhrases) {
						String key = pPhrase.select(">dt").first().ownText();// >dt selects the child dt tags.
						String value = pPhrase.select(">dd").first().ownText();
						info.put(key, value);
					}
				} catch (Exception e) {}
				apartments.add(tidy(info));
			}
			return apartments.stream()
					.map(x -> {
						x.setPricePerArea(x.getPrice() / x.getArea());
						return x;
					}).sorted(Comparator.comparing(Apartment::getPricePerArea)).collect(Collectors.toList());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Apartment tidy(Map<String, String> info) {
		log.info(info.toString());
		Apartment apartment = new Apartment();
		apartment.setPrice(new Double (info.get("price")));
		String area = info.get("面积");
		apartment.setArea(new Double(area.substring(0, area.length() - 2)));
		apartment.setUrl(info.get("url"));
		return apartment;
	}
	
	@Test
	public void test() {
		String url = "http://sz.zu.anjuke.com/ditie/zj21/";
		List<Apartment> apartments = search(url);
		System.out.println(apartments);
	}
}	

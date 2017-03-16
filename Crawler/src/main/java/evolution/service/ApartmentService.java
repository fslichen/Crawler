package evolution.service;

import java.util.List;
import java.util.Map;

public interface ApartmentService<V> {
	public List<V> search(String url);
	
	public V tidy(Map<String, String> info);
}

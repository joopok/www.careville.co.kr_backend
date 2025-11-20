package kr.co.cleaning.core.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

public class JsonView extends MappingJackson2JsonView{

	@Override
	protected Object filterModel(Map<String, Object> model) {
		Object result = super.filterModel(model);

		if(!(result instanceof Map) && !(result instanceof HashMap)) return result;

		Map map = (Map) result;

		if(!map.containsKey("isError")) map.put("isError", "false");
		if(map.containsKey("user")) map.remove("user");
		if(map.containsKey("user_info")) map.remove("user_info");

		return map;
	}

}

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.lx.service.OrderSecKillService;


@ContextConfiguration(value="classpath:spring/spring.xml")
public class InitStockServiceImplTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private OrderSecKillService orderSecKillService;
	
	@Test
	public void initSkuStockTest() {
		orderSecKillService.initSkuStock("1001", 50L);
	}

}

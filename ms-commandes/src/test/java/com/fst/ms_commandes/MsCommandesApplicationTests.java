package com.fst.ms_commandes;

import com.fst.ms_commandes.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestSecurityConfig.class)
class MsCommandesApplicationTests {

	@Test
	void contextLoads() {
	}

}

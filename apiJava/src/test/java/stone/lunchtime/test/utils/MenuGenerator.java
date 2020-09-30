// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.test.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;

import stone.lunchtime.dto.in.MenuDtoIn;

/**
 * Not a test class. Will generate menus.
 */
@Disabled("Not for tests, used for data base generation.")
public final class MenuGenerator {
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Constructor of the object.
	 */
	private MenuGenerator() {
		throw new IllegalAccessError("Not for use");
	}

	/**
	 * Generates menus
	 *
	 * @param pHowMany       number of elements to generate
	 * @param pUseDataForImg if data should be used for image or URL
	 * @return list of menus
	 */
	public static List<MenuDtoIn> generate(int pHowMany) {
		Random random = new Random();
		List<MenuDtoIn> result = new ArrayList<>(pHowMany);
		for (int i = 0; i < pHowMany; i++) {
			MenuGenerator.LOG.debug("Creating menu {}/{}", i, pHowMany);
			MenuDtoIn menu = new MenuDtoIn();
			menu.setLabel("Menu - " + i);
			menu.setPriceDF(BigDecimal.valueOf(random.nextDouble() * 20 + 5));
			result.add(menu);
		}
		return result;
	}
}

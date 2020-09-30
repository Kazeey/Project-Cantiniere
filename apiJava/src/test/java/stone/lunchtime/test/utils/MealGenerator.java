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

import stone.lunchtime.dto.in.ImageDtoIn;
import stone.lunchtime.dto.in.MealDtoIn;

/**
 * Not a test class. Will generate meals.
 */
@Disabled("Not for tests, used for data base generation.")
public final class MealGenerator {
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Constructor of the object.
	 */
	private MealGenerator() {
		throw new IllegalAccessError("Not for use");
	}

	/**
	 * Generates meals.
	 *
	 * @param pHowMany       the number to generate
	 * @return the list of meals
	 */
	public static List<MealDtoIn> generate(int pHowMany) {
		List<String> rawList = TestFileReader.readMeals();
		final int listSize = rawList.size();
		if (pHowMany > listSize) {
			MealGenerator.LOG.warn("Not enough meals in the file, will only generate {} meals", listSize);
			pHowMany = listSize;
		}

		Random random = new Random();
		List<MealDtoIn> result = new ArrayList<>(pHowMany);
		for (int i = 0; i < pHowMany; i++) {
			MealGenerator.LOG.debug("Creating meal {}/{}", i, pHowMany);
			MealDtoIn meal = new MealDtoIn();
			String rawLine = rawList.get(i);
			String[] splitedLine = rawLine.split("\t");
			if (splitedLine == null || splitedLine.length != 3) {
				MealGenerator.LOG.error("Error on line {} size found is [{}] {}", i,
						splitedLine != null ? splitedLine.length : "null", rawLine);
				continue;
			}
			meal.setLabel(splitedLine[0]);
			if (!"-".equals(splitedLine[1])) {
				meal.setDescription(splitedLine[1]);
			}

			String img = splitedLine[2];
			ImageDtoIn imgDto = new ImageDtoIn();
			if (!"-".equals(img)) {
				imgDto.setImage64(img);
				imgDto.setImagePath("img/meal/" + meal.getLabel() + ".png");
				meal.setImage(imgDto);
			}

			meal.setPriceDF(BigDecimal.valueOf(random.nextDouble() * 15 + 0.5));
			result.add(meal);
		}
		return result;
	}
}

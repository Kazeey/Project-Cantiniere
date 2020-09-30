// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.test.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;

import stone.lunchtime.dto.in.ImageDtoIn;
import stone.lunchtime.dto.in.IngredientDtoIn;

/**
 * Not a test class. Will generate ingredients.
 */
@Disabled("Not for tests, used for data base generation.")
public final class IngredientGenerator {
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Constructor of the object.
	 */
	private IngredientGenerator() {
		throw new IllegalAccessError("Not for use");
	}

	/**
	 * Generates ingredients.
	 *
	 * @param pHowMany       the number to generate
	 * @return the list of ingredients
	 */
	public static List<IngredientDtoIn> generate(int pHowMany) {
		List<String> rawList = TestFileReader.readIngredients();
		final int listSize = rawList.size();
		if (pHowMany > listSize) {
			IngredientGenerator.LOG.warn("Not enough ingredients in the file, will only generate {} ingredients",
					listSize);
			pHowMany = listSize;
		}

		List<IngredientDtoIn> result = new ArrayList<>(pHowMany);
		for (int i = 0; i < pHowMany; i++) {
			IngredientGenerator.LOG.debug("Creating ingredient {}/{}", i, pHowMany);
			IngredientDtoIn ingredient = new IngredientDtoIn();
			String rawLine = rawList.get(i);
			String[] splitedLine = rawLine.split("\t");
			if (splitedLine == null || splitedLine.length != 3) {
				IngredientGenerator.LOG.error("Error on line {} size found is [{}] {}", i,
						splitedLine != null ? splitedLine.length : "null", rawLine);
				continue;
			}
			ingredient.setLabel(splitedLine[0]);
			if (!"-".equals(splitedLine[1])) {
				ingredient.setDescription(splitedLine[1]);
			}
			String img = splitedLine[2];
			ImageDtoIn imgDto = new ImageDtoIn();
			if (!"-".equals(img)) {
				imgDto.setImage64(img);
				imgDto.setImagePath("img/ingredient/" + ingredient.getLabel() + ".png");
				ingredient.setImage(imgDto);
			}

			result.add(ingredient);
		}
		return result;
	}
}

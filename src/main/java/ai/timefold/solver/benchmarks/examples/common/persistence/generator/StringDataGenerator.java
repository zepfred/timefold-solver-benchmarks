package ai.timefold.solver.benchmarks.examples.common.persistence.generator;

import java.util.ArrayList;
import java.util.List;

public class StringDataGenerator {

    public static StringDataGenerator buildFullNames() {
        return new StringDataGenerator()
                .addPart(true, 0,
                        "Amy",
                        "Beth",
                        "Carl",
                        "Dan",
                        "Elsa",
                        "Flo",
                        "Gus",
                        "Hugo",
                        "Ivy",
                        "Jay")
                .addPart(false, 1,
                        "A.",
                        "B.",
                        "C.",
                        "D.",
                        "E.",
                        "F.",
                        "G.",
                        "H.",
                        "I.",
                        "J.")
                .addPart(false, 1,
                        "O.",
                        "P.",
                        "Q.",
                        "R.",
                        "S.",
                        "T.",
                        "U.",
                        "V.",
                        "W.",
                        "X.")
                .addPart(false, 1,
                        "Cole",
                        "Fox",
                        "Green",
                        "Jones",
                        "King",
                        "Li",
                        "Poe",
                        "Rye",
                        "Smith",
                        "Watt");
    }

    public static StringDataGenerator buildCompanyNames() {
        return new StringDataGenerator()
                .addPart(true, 0,
                        "Steel",
                        "Paper",
                        "Stone",
                        "Wood",
                        "Water",
                        "Food",
                        "Oil",
                        "Car",
                        "Power",
                        "Computer")
                .addPart(true, 1,
                        "Inc",
                        "Corp",
                        "Limited",
                        "Express",
                        "Telco",
                        "Mobile",
                        "Soft",
                        "Mart",
                        "Bank",
                        "Labs")
                .addPart(false, 2,
                        "US",
                        "UK",
                        "JP",
                        "DE",
                        "FR",
                        "BE",
                        "NL",
                        "BR",
                        "IN",
                        "ES");
    }

    /**
     * Determines how to go through the unique combinations to maximize uniqueness, even on small subsets.
     * It does not scroll per digit (0000, 1111, 2222, 0001, 1112, 2220, 0002, 1110, 2221, ...).
     * Instead, it scrolls per half (0000, 1111, 2222, 0011, 1122, 2200, 0022, 1100, 2211, ...).
     */
    private final static int[][] HALF_SEQUENCE_MAP = new int[][] { {}, { 0 }, { 0, 1 }, { 0, 2, 1 }, { 0, 2, 1, 3 } };
    /**
     * Determines which parts to eliminate first if maximumSize prediction doesn't need all parts.
     */
    private final static int[] DEFAULT_ELIMINATION_INDEX_MAP = new int[] { 0, 1, 1, 1 };

    private final boolean capitalizeFirstLetter;
    private final String delimiter;
    private final List<String[]> partValuesList = new ArrayList<>();
    private int partValuesLength;
    private final List<Integer> eliminationIndexMap = new ArrayList<>();
    private int requiredSize = 0;

    private List<String[]> filteredPartValuesList = partValuesList;
    private int index = 0;
    private int indexLimit;

    public StringDataGenerator() {
        this(false);
    }

    public StringDataGenerator(boolean capitalizeFirstLetter) {
        this(capitalizeFirstLetter, " ");
    }

    public StringDataGenerator(String delimiter) {
        this(false, delimiter);
    }

    public StringDataGenerator(boolean capitalizeFirstLetter, String delimiter) {
        this.capitalizeFirstLetter = capitalizeFirstLetter;
        this.delimiter = delimiter;
    }

    public StringDataGenerator addPart(String... partValues) {
        return addPart(false, DEFAULT_ELIMINATION_INDEX_MAP[partValuesList.size()], partValues);
    }

    public StringDataGenerator addAToZPart() {
        return addAToZPart(false, DEFAULT_ELIMINATION_INDEX_MAP[partValuesList.size()]);
    }

    public StringDataGenerator addAToZPart(boolean required, int eliminationIndex) {
        return addPart(required, eliminationIndex,
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z");
    }

    public StringDataGenerator addNumericPart(boolean required, int eliminationIndex, int from, int to) {
        String[] partValues = new String[to - from];
        for (int i = from; i < to; i++) {
            partValues[i - from] = Integer.toString(i);
        }
        return addPart(required, eliminationIndex, partValues);
    }

    public StringDataGenerator addPart(boolean required, int eliminationIndex, String... partValues) {
        if (partValuesList.isEmpty()) {
            partValuesLength = partValues.length;
        } else {
            if (partValues.length != partValuesLength) {
                throw new IllegalStateException("The partValues length (" + partValues.length
                        + ") is not the same as the partValuesLength (" + partValuesLength + ") of the others.");
            }
        }
        if (required) {
            requiredSize++;
        }
        partValuesList.add(partValues);
        eliminationIndexMap.add(eliminationIndex);
        indexLimit = (int) Math.pow(partValuesLength, partValuesList.size());
        filteredPartValuesList = partValuesList;
        return this;
    }

    public void reset() {
        filteredPartValuesList = partValuesList;
        index = 0;
    }

    public void predictMaximumSizeAndReset(int maximumSize) {
        indexLimit = (int) Math.pow(partValuesLength, partValuesList.size());
        filteredPartValuesList = partValuesList;
        for (int i = 1; i < partValuesList.size(); i++) {
            int proposedIndexLimit = (int) Math.pow(partValuesLength, i);
            if (maximumSize <= proposedIndexLimit) {
                filteredPartValuesList = new ArrayList<>(partValuesList);
                while (i < filteredPartValuesList.size() && filteredPartValuesList.size() > requiredSize) {
                    int eliminationIndex = eliminationIndexMap.get(filteredPartValuesList.size() - 1);
                    filteredPartValuesList.remove(eliminationIndex);
                }
                indexLimit = proposedIndexLimit;
                break;
            }
        }
        index = 0;
    }

    public String generateNextValue() {
        if (index >= indexLimit) {
            throw new IllegalStateException("No more elements: the index (" + index
                    + ") is higher than the indexLimit (" + indexLimit + ").\n"
                    + "Maybe predictMaximumSizeAndReset() was called with a too low maximumSize.");
        }
        int listSize = filteredPartValuesList.size();
        StringBuilder result = new StringBuilder(listSize * 80);
        // Make sure we have a unique combination
        if (listSize >= HALF_SEQUENCE_MAP.length) {
            throw new IllegalStateException("A listSize (" + listSize + ") is not yet supported.");
        }
        int[] halfSequence = HALF_SEQUENCE_MAP[listSize];
        int[] chosens = new int[listSize];
        int previousChosen = 0;
        for (int i = 0; i < listSize; i++) {
            int chosen = (previousChosen
                    + (index % (int) Math.pow(partValuesLength, halfSequence[i] + 1)
                            / (int) Math.pow(partValuesLength, halfSequence[i])))
                    % partValuesLength;
            chosens[i] = chosen;
            previousChosen = chosen;
        }
        for (int i = 0; i < listSize; i++) {
            if (i > 0) {
                result.append(delimiter);
            }
            String[] partValues = filteredPartValuesList.get(i);
            result.append(partValues[chosens[i]]);
        }
        index++;
        if (capitalizeFirstLetter) {
            result.setCharAt(0, Character.toUpperCase(result.charAt(0)));
        }
        return result.toString();
    }

}

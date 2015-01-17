package calories.tracker.app.model;


import java.util.List;

/**
 * Search result of a search method, meant to be used as return type in the service layer for search methods,
 * when both the result and the total results count are needed.
 *
 * @param <T>
 */
public class SearchResult<T> {

    private long resultsCount;
    private List<T> result;

    public SearchResult(long resultsCount, List<T> result) {
        this.resultsCount = resultsCount;
        this.result = result;
    }

    public long getResultsCount() {
        return resultsCount;
    }

    public List<T> getResult() {
        return result;
    }
}

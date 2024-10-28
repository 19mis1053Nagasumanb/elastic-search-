package com.example.taskmangementsystem.g.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.taskmangementsystem.g.entity.Task;
import com.example.taskmangementsystem.g.util.ESUtil;
import com.example.taskmangementsystem.g.util.ElasticSearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Supplier;
@Service
public class ESService {
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public SearchResponse<Task>fuzzySearch(String approximateTaskName,String fuzziness) throws IOException {
        Supplier<Query> supplier = ElasticSearchUtil.createSupplierQuery(approximateTaskName,fuzziness);
        SearchResponse<Task>response = elasticsearchClient
                .search(s->s.index("optimizedes").query(supplier.get()),Task.class);
        System.out.println("elasticsearch supplier fuzzy query "+supplier.get().toString());
        return response;
    }

//    public SearchResponse<Task> searchWithSorting(String searchTerm, String sortField, String sortOrder,String fuzziness) throws IOException {
//        Supplier<Query> supplier = ElasticSearchUtil.createSupplierQuery(searchTerm,fuzziness);
//
//        // Elasticsearch client call with sort functionality
//        SearchResponse<Task> response = elasticsearchClient.search(s -> s
//                .index("optimizedes")
//                .query(supplier.get())
//                .sort(so -> so
//                        .field(f -> f
//                                .field(sortField)
//                                .order(sortOrder.equalsIgnoreCase("asc") ? SortOrder.Asc : SortOrder.Desc)) // Set the sort order based on the string
//                ), Task.class);
//
//        return response;
//    }

//    public List<Map<String, Object>> searchWithSorting(String searchTerm, String sortField, String sortOrder, String fuzziness) throws IOException {
//
//        System.out.println("Sorting by: " +sortField+ " in " + sortOrder + " order");
//        String fieldToSort = sortField.equals("name") ? "name.keyword" : sortField;
//
//        // Construct the search query with sorting
//
//
//        SearchResponse<Map> searchResponse = elasticsearchClient.search(s -> s
//                        .index("optimizedes")
//                        .query(q -> q
//                                .match(m -> m
//                                        .field("name")
//                                        .query(searchTerm)
//                                        .fuzziness(fuzziness)
//                                )
//                        )
//                        .sort(sort -> sort
//                                .field(f -> f
//                                        .field(fieldToSort)
//                                        .order(sortOrder.equals("asc") ? SortOrder.Asc : SortOrder.Desc)
//                                )
//                        ),
//                Map.class);
//
//        List<Hit<Map>> hits = searchResponse.hits().hits();
//        List<Map<String, Object>> resultList = new ArrayList<>();
//
//        for (Hit<Map> hit : hits) {
//            resultList.add(hit.source());
//        }
//
//        return resultList;
//    }

    //for suggestions


    public SearchResponse<Task> autoSuggestTask(String searchTerm, String field) throws IOException {
        Supplier<Query> supplier = ESUtil.createSupplierAutoSuggest(searchTerm, field);
        SearchResponse<Task> searchResponse = elasticsearchClient
                .search(s -> s.index("optimizedes").query(supplier.get()), Task.class);
        System.out.println("Elasticsearch auto-suggestion query: " + supplier.get().toString());
        return searchResponse;
    }

}

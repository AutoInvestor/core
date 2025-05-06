package io.autoinvestor.infrastructure;

import io.autoinvestor.domain.Asset;
import io.autoinvestor.domain.AssetRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@Profile("prod")
public class MongoAssetRepository implements AssetRepository {

    private final MongoTemplate mongoTemplate;

    public MongoAssetRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(Asset asset) {
        mongoTemplate.save(asset);
    }

    @Override
    public boolean exists(String mic, String ticker) {
        Query q = new Query(Criteria
                .where("mic").is(mic)
                .and("ticker").is(ticker)
        );
        return mongoTemplate.exists(q, Asset.class);
    }
}

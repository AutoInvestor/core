package io.autoinvestor.infrastructure.repositories;

import io.autoinvestor.domain.Asset;
import io.autoinvestor.domain.AssetId;
import io.autoinvestor.domain.AssetRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
@Profile("prod")
class MongoAssetRepository implements AssetRepository {

    private final MongoTemplate template;
    private final AssetMapper mapper;

    public MongoAssetRepository(MongoTemplate template, AssetMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    @Override
    public void save(Asset asset) {
        template.save(mapper.toDocument(asset));
    }

    @Override
    public boolean exists(String mic, String ticker) {
        var q = Query.query(Criteria
                .where("mic").is(mic)
                .and("ticker").is(ticker));
        return template.exists(q, AssetDocument.class);
    }

    @Override
    public Optional<Asset> findById(AssetId assetId) {
        return Optional.ofNullable(template.findById(assetId.value(), AssetDocument.class))
                .map(mapper::toDomain);
    }

    @Override
    public List<Asset> findAll() {
        var q = new Query();
        var assetDocuments = template.find(q, AssetDocument.class);
        List<Asset> assets = new ArrayList<>();
        for (AssetDocument assetDocument : assetDocuments) {
            assets.add(mapper.toDomain(assetDocument));
        }
        return assets;
    }
}

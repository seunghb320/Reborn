package spring.reborn.domain.store;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponseStatus;
import spring.reborn.domain.store.model.GetStoreLocationRes;
import spring.reborn.domain.store.model.GetStoreRes;
import spring.reborn.domain.store.model.PatchStoreReq;
import spring.reborn.domain.store.model.StoreCategory;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class StoreDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetStoreRes> getStoreList() throws BaseException {
        try {

            String getStoreListQuery = "SELECT storeIdx, storeName,storeImage, storeAddress, storeDescription, category, storeScore FROM Store WHERE status = 'ACTIVE' ORDER BY updatedAt desc";
            List<GetStoreRes> res = this.jdbcTemplate.query(
                    getStoreListQuery,
                    (rs, rowNum) -> GetStoreRes.builder()
                            .storeIdx(rs.getLong("storeIdx"))
                            .storeName(rs.getString("storeName"))
                            .category(StoreCategory.valueOf(rs.getString("category")))
                            .storeAddress(rs.getString("storeAddress"))
                            .storeImage(rs.getString("storeImage"))
                            .storeDescription(rs.getString("storeDescription"))
                            .storeScore(rs.getFloat("storeScore"))

                            .build()

            );
            return res;

        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);

        }

    }


    public GetStoreLocationRes getStoreLocation(Long storeIdx) throws BaseException {
        try {
            String getStoreQuery = "SELECT storeIdx, storeName, storeAddress, storeScore FROM Store WHERE storeIdx = ? and status = 'ACTIVE'";

            Object[] selectStoreParams = new Object[]{storeIdx};
            GetStoreLocationRes res = this.jdbcTemplate.queryForObject(
                    getStoreQuery,
                    selectStoreParams,
                    (rs, rowNum) -> GetStoreLocationRes.builder()
                            .storeIdx(rs.getLong("storeIdx"))
                            .storeName(rs.getString("storeName"))
                            .storeAddress(rs.getString("storeAddress"))
                            .storeScore(rs.getFloat("storeScore"))

                            .build()

            );
            return res;

        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.CAN_NOT_FOUND_STORE);
        }
    }

    public GetStoreRes getStoreInfo(Long storeIdx) throws BaseException {
        try {
            String getStoreInfoQuery = "SELECT storeIdx, storeName ,storeImage, storeAddress, storeDescription, category, storeScore FROM Store WHERE storeIdx = ? and status = 'ACTIVE'";

            Object[] selectStoreParams = new Object[]{storeIdx};

            GetStoreRes res = this.jdbcTemplate.queryForObject(
                    getStoreInfoQuery,
                    selectStoreParams,
                    (rs, rowNum) -> GetStoreRes.builder()
                            .storeIdx(rs.getLong("storeIdx"))
                            .storeName(rs.getString("storeName"))
                            .category(StoreCategory.valueOf(rs.getString("category")))
                            .storeAddress(rs.getString("storeAddress"))
                            .storeImage(rs.getString("storeImage"))
                            .storeDescription(rs.getString("storeDescription"))
                            .storeScore(rs.getFloat("storeScore"))

                            .build()

            );
            return res;

        }
        catch(Exception e){
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.CAN_NOT_FOUND_STORE);

        }

    }

    public List<GetStoreRes> searchStoreUsingTitle(String keyword) throws BaseException {
        try {

            String getStoreInfoQuery = "SELECT storeIdx, storeName ,storeImage, storeAddress, storeDescription, category, storeScore FROM Store WHERE UPPER(storeName) LIKE UPPER(?) and status = 'ACTIVE'";

            String paramString = "%" + keyword + "%";
            Object[] selectStoreParams = new Object[]{paramString};

            List<GetStoreRes> res = this.jdbcTemplate.query(
                    getStoreInfoQuery,
                    selectStoreParams,
                    (rs, rowNum) -> GetStoreRes.builder()
                            .storeIdx(rs.getLong("storeIdx"))
                            .storeName(rs.getString("storeName"))
                            .category(StoreCategory.valueOf(rs.getString("category")))
                            .storeAddress(rs.getString("storeAddress"))
                            .storeImage(rs.getString("storeImage"))
                            .storeDescription(rs.getString("storeDescription"))
                            .storeScore(rs.getFloat("storeScore"))

                            .build()

            );
            return res;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.SEARCH_STORE_ERROR);
        }
    }


    public List<GetStoreRes> searchStoreUsingTitleSortByName(String keyword) throws BaseException{
        try {

            String getStoreInfoQuery = "SELECT storeIdx, storeName ,storeImage, storeAddress, storeDescription, category, storeScore " +
                    "FROM Store " +
                    "WHERE UPPER(storeName) LIKE UPPER(?) and status = 'ACTIVE' " +
                    "ORDER BY storeName ASC ";

            String paramString = "%" + keyword + "%";
            Object[] selectStoreParams = new Object[]{paramString};

            List<GetStoreRes> res = this.jdbcTemplate.query(
                    getStoreInfoQuery,
                    selectStoreParams,
                    (rs, rowNum) -> GetStoreRes.builder()
                            .storeIdx(rs.getLong("storeIdx"))
                            .storeName(rs.getString("storeName"))
                            .category(StoreCategory.valueOf(rs.getString("category")))
                            .storeAddress(rs.getString("storeAddress"))
                            .storeImage(rs.getString("storeImage"))
                            .storeDescription(rs.getString("storeDescription"))
                            .storeScore(rs.getFloat("storeScore"))

                            .build()

            );
            return res;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.SEARCH_STORE_ERROR);
        }
    }
    public List<GetStoreRes> searchStoreUsingTitleSortByScore(String keyword) throws BaseException{
        try {

            String getStoreInfoQuery = "SELECT storeIdx, storeName ,storeImage, storeAddress, storeDescription, category, storeScore " +
                    "FROM Store " +
                    "WHERE UPPER(storeName) LIKE UPPER(?) and status = 'ACTIVE' " +
                    "ORDER BY storeScore Desc ";

            String paramString = "%" + keyword + "%";
            Object[] selectStoreParams = new Object[]{paramString};

            List<GetStoreRes> res = this.jdbcTemplate.query(
                    getStoreInfoQuery,
                    selectStoreParams,
                    (rs, rowNum) -> GetStoreRes.builder()
                            .storeIdx(rs.getLong("storeIdx"))
                            .storeName(rs.getString("storeName"))
                            .category(StoreCategory.valueOf(rs.getString("category")))
                            .storeAddress(rs.getString("storeAddress"))
                            .storeImage(rs.getString("storeImage"))
                            .storeDescription(rs.getString("storeDescription"))
                            .storeScore(rs.getFloat("storeScore"))

                            .build()

            );
            return res;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.SEARCH_STORE_ERROR);
        }
    }
    public List<GetStoreRes> searchStoreUsingTitleSortByJjim(String keyword) throws BaseException{
        try {

            String getStoreInfoQuery = "SELECT s.storeIdx, storeName ,storeImage, storeAddress, storeDescription, category, storeScore " +
            "FROM Store s LEFT JOIN Jjim j ON s.storeIdx = j.storeIdx " +
            "WHERE UPPER(storeName) LIKE UPPER(?) and s.status = 'ACTIVE' " +
            "GROUP BY s.storeIdx " +
            "ORDER BY COUNT(j.storeIdx) DESC ";

            String paramString = "%" + keyword + "%";
            Object[] selectStoreParams = new Object[]{paramString};

            List<GetStoreRes> res = this.jdbcTemplate.query(
                    getStoreInfoQuery,
                    selectStoreParams,
                    (rs, rowNum) -> GetStoreRes.builder()
                            .storeIdx(rs.getLong("storeIdx"))
                            .storeName(rs.getString("storeName"))
                            .category(StoreCategory.valueOf(rs.getString("category")))
                            .storeAddress(rs.getString("storeAddress"))
                            .storeImage(rs.getString("storeImage"))
                            .storeDescription(rs.getString("storeDescription"))
                            .storeScore(rs.getFloat("storeScore"))

                            .build()

            );
            return res;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.SEARCH_STORE_ERROR);
        }
    }

    @Transactional
    public void updateStoreInfo(Long storeIdx, PatchStoreReq patchStoreReq) throws BaseException {
        try {
            String updateStoreInfoQuery = "UPDATE Store SET storeName = ?, storeAddress = ?, storeDescription = ?, category = ?, storeImage = ? WHERE storeIdx = ? and status = 'ACTIVE'";

            Object[] updateStoreParams = new Object[]{
                    patchStoreReq.getStoreName(),
                    patchStoreReq.getStoreAddress(),
                    patchStoreReq.getStoreDescription(),
                    patchStoreReq.getCategory(),
                    patchStoreReq.getStoreImage(),
                    storeIdx
            };


            this.jdbcTemplate.update(
                    updateStoreInfoQuery,
                    updateStoreParams
            );

            updateStoreUpdateTime(storeIdx);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.MODIFY_FAIL_STORE);
        }


    }
    public void updateStoreUpdateTime(Long storeIdx) throws BaseException {
        try {
            String updateStoreUpdateAtQuery = "UPDATE Store SET updatedAt = ? WHERE storeIdx = ? and status = 'ACTIVE'";

            Object[] updateStoreParams = new Object[]{
                    new Timestamp(new Date().getTime()),
                    storeIdx
            };


            this.jdbcTemplate.update(
                    updateStoreUpdateAtQuery,
                    updateStoreParams
            );

        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.MODIFY_FAIL_STORE_UPDATE_TIME);
        }

    }

}

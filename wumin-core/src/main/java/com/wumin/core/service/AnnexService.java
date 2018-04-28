package com.wumin.core.service;

import com.wumin.common.collection.ArrayUtil;
import com.wumin.common.collection.ListUtil;
import com.wumin.common.service.impl.JpaServiceImpl;
import com.wumin.common.web.FileRepository;
import com.wumin.core.dao.AnnexDao;
import com.wumin.core.entity.Annex;
import com.wumin.core.entity.QAnnex;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 附件管理业务类.
 *
 * @author walker
 */
// Spring Service Bean的标识.
@Service
public class AnnexService extends JpaServiceImpl<Annex, String> {

  @Autowired
  private AnnexDao annexDao;
  @Autowired
  private FileRepository fileRepository;

  public Annex getByPath(String path) {
    return annexDao.findByPath(path);
  }

  public void delete(String id) {
    Annex annex = get(id);
    this.delete(annex);
  }

  public void deleteInBatch(final String[] ids) {
    if (ArrayUtils.isEmpty(ids)) {
      return;
    }
    List<Annex> annexs = dao.findAllById(ArrayUtil.asList(ids));
    for(Annex annex: annexs) {
      this.delete(annex);
    }
  }

  public void delete(Annex annex) {
    fileRepository.deleteRelevantByPath(annex.getPath());
    super.delete(annex);
  }

  public void deleteByObject(String objectId, String objectType) {
    List<Annex> annexs = findByObject(objectId, objectType);
    for (Annex annex : annexs) {
      this.delete(annex);
    }
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public Annex copy(Annex annex, String path, String objectId, String objectType) throws IOException {
    String filePath = fileRepository.storeByPath(annex.getPath(), path);

    Annex entity = new Annex();
    entity.setObjectId(objectId);
    entity.setObjectType(objectType);
    entity.setPath(filePath);
    entity.setName(annex.getName());
    entity.setType(annex.getType());
    save(entity);

    return entity;
  }

  public Annex save(BufferedImage image, String fileName, String path, String objectId, String objectType)
          throws IOException {
    String ext = FilenameUtils.getExtension(fileName).toLowerCase(Locale.ENGLISH);
    String filePath = fileRepository.storeByExt(image, path, ext);

    Annex entity = new Annex();
    entity.setObjectId(objectId);
    entity.setObjectType(objectType);
    entity.setPath(filePath);
    entity.setName(fileName);
    entity.setType(ext);
    save(entity);

    return entity;
  }

  public Annex save(byte[] data, String fileName, String path, String objectId, String objectType)
          throws IOException {
    String ext = FilenameUtils.getExtension(fileName).toLowerCase(Locale.ENGLISH);
    String filePath = fileRepository.storeByExt(data, path, ext);

    Annex entity = new Annex();
    entity.setObjectId(objectId);
    entity.setObjectType(objectType);
    entity.setPath(filePath);
    entity.setName(fileName);
    entity.setType(ext);
    save(entity);

    return entity;
  }

  public Annex save(byte[] data, String fileName, String objectId, String objectType) throws IOException {
    String filePath = fileRepository.storeByFilename(data, fileName);

    Annex entity = new Annex();
    entity.setObjectId(objectId);
    entity.setObjectType(objectType);
    entity.setPath(filePath);
    entity.setName(FilenameUtils.getName(fileName));
    entity.setType(FilenameUtils.getExtension(fileName).toLowerCase(Locale.ENGLISH));
    save(entity);

    return entity;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public Annex move(String id, String objectId, String objectType, String path) throws IOException {
    Annex entity = get(id);
    String filePath = fileRepository.moveByPath(entity.getPath(), path);

    entity.setObjectId(objectId);
    entity.setObjectType(objectType);
    entity.setPath(filePath);
    save(entity);

    return entity;
  }

  public Page<Annex> getPage(final String searchKey, final Date startTime, final Date endTime, Pageable page) {
    QAnnex qAnnex = QAnnex.annex;
    List<BooleanExpression> expressions = new ArrayList<>();

    if (StringUtils.isNotEmpty(searchKey)) {
      expressions.add(qAnnex.objectType.like(StringUtils.trimToEmpty(searchKey))
        .or(qAnnex.path.like(StringUtils.trimToEmpty(searchKey)))
        .or(qAnnex.name.like(StringUtils.trimToEmpty(searchKey)))
        .or(qAnnex.type.like(StringUtils.trimToEmpty(searchKey))));
    }

    if (startTime != null && endTime != null) {
      expressions.add(qAnnex.createTime.between(startTime, endTime));
    } else if (startTime != null) {
      expressions.add(qAnnex.createTime.goe(startTime));
    } else if (endTime != null) {
      expressions.add(qAnnex.createTime.loe(endTime));
    }

    return annexDao.findAll(Expressions.allOf(expressions.toArray(new BooleanExpression[expressions.size()])), page);
  }

  public List<Annex> findByObject(final String objectId, final String objectType) {
    QAnnex qAnnex = QAnnex.annex;
    List<BooleanExpression> expressions = new ArrayList<>();
    expressions.add(qAnnex.objectId.eq(objectId));
    expressions.add(qAnnex.objectType.eq(objectType));
    return ListUtil.newArrayList(annexDao.findAll(Expressions.allOf(expressions.toArray(new BooleanExpression[expressions.size()]))));
  }

}

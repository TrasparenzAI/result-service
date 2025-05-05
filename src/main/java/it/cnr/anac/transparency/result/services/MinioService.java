/**
 * Servizio per effettuare le operazioni con il minio.
 *
 * @author Cristian Lucchesi
 */
package it.cnr.anac.transparency.result.services;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import it.cnr.anac.transparency.result.models.StorageData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servizi per la rimozione del codice sorgente e screenshot salvati
 * nel minio.
 *
 * @author Cristian Lucchesi
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MinioService {

  private final MinioClient minioClient;

  @Async
  public CompletableFuture<Integer> removeObjects(List<StorageData> storageDataList) {
    log.info("Objects to remove = {}", storageDataList.size());
    int deleted = 0;
    for (StorageData storageData : storageDataList) {
      deleted += deleteStorageData(storageData);
    }
    return CompletableFuture.completedFuture(deleted);
  }

  public Integer deleteStorageData(StorageData storageData) {
    int deleted = 0;
    if (!Strings.isNullOrEmpty(storageData.getObjectBucket())) {
      if (removeSource(storageData)) {
        deleted++;
      }
    }

    if (!Strings.isNullOrEmpty(storageData.getScreenshotBucket())) {
      if (removeScreenshot(storageData)) {
        deleted++;
      }
    }
    return deleted;
  }

  private boolean removeSource(StorageData storageData) {
    log.debug("Removing source for storageData {}", storageData);
    try {
      minioClient.removeObject(
          RemoveObjectArgs.builder()
            .bucket(storageData.getObjectBucket()).object(storageData.getObjectId()).build());
      log.info("Eliminato sorgente html da storage esterno bucket = {}, id = {}", 
          storageData.getObjectBucket(), storageData.getObjectId());
      return true;
    } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
        | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
        | IllegalArgumentException | IOException e) {
      log.error("Unabled to remove source bucket = {}, id = {}", 
          storageData.getObjectBucket(), storageData.getObjectId(), e);
      return false;
    }
  }

  private boolean removeScreenshot(StorageData storageData) {
    log.debug("Removing screenshot for storageData {}", storageData);
    try {
      minioClient.removeObject(
          RemoveObjectArgs.builder()
            .bucket(storageData.getScreenshotBucket()).object(storageData.getScreenshotId()).build());
      log.info("Eliminata screenshot da storage esterno bucket = {}, id = {}", 
          storageData.getScreenshotBucket(), storageData.getScreenshotId());
      return true;
    } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
        | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
        | IllegalArgumentException | IOException e) {
      log.error("Unabled to remove source bucket = {}, id = {}", 
          storageData.getScreenshotBucket(), storageData.getScreenshotId(), e);
      return false;
    }
  }

}
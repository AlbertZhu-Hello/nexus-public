/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.repository.storage;

import java.io.IOException;

import javax.inject.Provider;

import org.sonatype.goodies.testsupport.TestSupport;
import org.sonatype.nexus.blobstore.api.BlobId;
import org.sonatype.nexus.blobstore.api.BlobRef;
import org.sonatype.nexus.common.entity.EntityId;
import org.sonatype.nexus.common.entity.EntityMetadata;
import org.sonatype.nexus.common.io.InputStreamSupplier;
import org.sonatype.nexus.common.node.NodeAccess;
import org.sonatype.nexus.mime.MimeRulesSource;
import org.sonatype.nexus.repository.config.WritePolicy;
import org.sonatype.nexus.repository.mime.DefaultContentValidator;
import org.sonatype.nexus.repository.move.RepositoryMoveService;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.tx.OTransaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Tests for {@link StorageTxImpl}.
 */
public class StorageTxImplTestNew extends TestSupport
{
  @Mock
  private BlobTx blobTx;

  @Mock
  private ODatabaseDocumentTx db;

  @Mock
  private OTransaction tx;

  @Mock
  private BucketEntityAdapter bucketEntityAdapter;

  @Mock
  private ComponentEntityAdapter componentEntityAdapter;

  @Mock
  private AssetEntityAdapter assetEntityAdapter;

  @Mock
  private ComponentFactory componentFactory;

  @Mock
  private Provider<RepositoryMoveService> repositoryMoveStoreProvider;

  @Mock
  private Asset asset;

  @Mock
  private EntityMetadata entityMetadata;

  @Mock
  private EntityId entityId;

  @Mock
  private NodeAccess nodeAccess;

  @Mock
  private DefaultContentValidator defaultContentValidator;

  @Mock
  private BlobRef blobRef;

  @Mock
  private BlobId blobId;

  @Before
  public void setup() throws IOException {
    when(asset.getEntityMetadata()).thenReturn(entityMetadata);
    when(asset.blobRef()).thenReturn(blobRef);
    when(blobRef.getBlobId()).thenReturn(blobId);

    when(entityMetadata.getId()).thenReturn(entityId);

    when(defaultContentValidator
        .determineContentType(anyBoolean(), any(InputStreamSupplier.class), eq(MimeRulesSource.NOOP), anyString(),
            anyString())).thenReturn("text/plain");
    when(db.getTransaction()).thenReturn(tx);
  }

  @Test
  public void saveAssetAttachesMetadata() {
    StorageTxImpl underTest = new StorageTxImpl("test", "127.0.0.1", blobTx, db, "testRepo", WritePolicy.ALLOW,
        WritePolicySelector.DEFAULT, bucketEntityAdapter, componentEntityAdapter, assetEntityAdapter, false,
        defaultContentValidator, MimeRulesSource.NOOP, componentFactory, repositoryMoveStoreProvider, nodeAccess);
    StorageTxImpl underTestSpy = spy(underTest);
    underTestSpy.saveAsset(asset);
    verify(underTestSpy).attachAssetMetadata(asset, blobId);
  }
}

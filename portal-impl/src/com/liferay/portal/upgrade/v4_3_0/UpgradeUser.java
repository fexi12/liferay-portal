/**
 * Copyright (c) 2000-2007 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.upgrade.v4_3_0;

import com.liferay.mail.model.CyrusUser;
import com.liferay.mail.model.CyrusVirtual;
import com.liferay.portal.model.User;
import com.liferay.portal.model.impl.AccountImpl;
import com.liferay.portal.model.impl.AddressImpl;
import com.liferay.portal.model.impl.ContactImpl;
import com.liferay.portal.model.impl.EmailAddressImpl;
import com.liferay.portal.model.impl.GroupImpl;
import com.liferay.portal.model.impl.PasswordTrackerImpl;
import com.liferay.portal.model.impl.PhoneImpl;
import com.liferay.portal.model.impl.SubscriptionImpl;
import com.liferay.portal.model.impl.UserIdMapperImpl;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.model.impl.UserTrackerImpl;
import com.liferay.portal.model.impl.WebsiteImpl;
import com.liferay.portal.tools.util.DBUtil;
import com.liferay.portal.upgrade.UpgradeException;
import com.liferay.portal.upgrade.UpgradeProcess;
import com.liferay.portal.upgrade.util.DefaultPKMapper;
import com.liferay.portal.upgrade.util.DefaultUpgradeTableImpl;
import com.liferay.portal.upgrade.util.PKUpgradeColumnImpl;
import com.liferay.portal.upgrade.util.SkipUpgradeColumnImpl;
import com.liferay.portal.upgrade.util.SwapUpgradeColumnImpl;
import com.liferay.portal.upgrade.util.TempUpgradeColumnImpl;
import com.liferay.portal.upgrade.util.UpgradeColumn;
import com.liferay.portal.upgrade.util.UpgradeTable;
import com.liferay.portal.upgrade.util.ValueMapper;
import com.liferay.portal.upgrade.v4_3_0.util.ClassPKUpgradeColumnImpl;
import com.liferay.portlet.blogs.model.impl.BlogsCategoryImpl;
import com.liferay.portlet.blogs.model.impl.BlogsEntryImpl;
import com.liferay.portlet.bookmarks.model.impl.BookmarksEntryImpl;
import com.liferay.portlet.bookmarks.model.impl.BookmarksFolderImpl;
import com.liferay.portlet.calendar.model.impl.CalEventImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileRankImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileShortcutImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileVersionImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderImpl;
import com.liferay.portlet.imagegallery.model.impl.IGFolderImpl;
import com.liferay.portlet.imagegallery.model.impl.IGImageImpl;
import com.liferay.portlet.journal.model.impl.JournalArticleImpl;
import com.liferay.portlet.journal.model.impl.JournalStructureImpl;
import com.liferay.portlet.journal.model.impl.JournalTemplateImpl;
import com.liferay.portlet.messageboards.model.impl.MBCategoryImpl;
import com.liferay.portlet.messageboards.model.impl.MBMessageFlagImpl;
import com.liferay.portlet.messageboards.model.impl.MBMessageImpl;
import com.liferay.portlet.messageboards.model.impl.MBStatsUserImpl;
import com.liferay.portlet.messageboards.model.impl.MBThreadImpl;
import com.liferay.portlet.polls.model.impl.PollsQuestionImpl;
import com.liferay.portlet.polls.model.impl.PollsVoteImpl;
import com.liferay.portlet.ratings.model.impl.RatingsEntryImpl;
import com.liferay.portlet.shopping.model.impl.ShoppingCartImpl;
import com.liferay.portlet.shopping.model.impl.ShoppingCategoryImpl;
import com.liferay.portlet.shopping.model.impl.ShoppingCouponImpl;
import com.liferay.portlet.shopping.model.impl.ShoppingItemImpl;
import com.liferay.portlet.shopping.model.impl.ShoppingOrderImpl;
import com.liferay.portlet.wiki.model.impl.WikiNodeImpl;
import com.liferay.portlet.wiki.model.impl.WikiPageImpl;

import java.sql.Types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <a href="UpgradeUser.java.html"><b><i>View Source</i></b></a>
 *
 * @author Alexander Chow
 * @author Brian Wing Shun Chan
 *
 */
public class UpgradeUser extends UpgradeProcess {

	public void upgrade() throws UpgradeException {
		_log.info("Upgrading");

		try {
			_upgrade();
		}
		catch (Exception e) {
			throw new UpgradeException(e);
		}
	}

	private void _upgrade() throws Exception {

		// User_

		PKUpgradeColumnImpl upgradeColumn =
			new PKUpgradeColumnImpl(0, new Integer(Types.VARCHAR), true);

		UpgradeTable upgradeTable = new DefaultUpgradeTableImpl(
			UserImpl.TABLE_NAME, UserImpl.TABLE_COLUMNS, upgradeColumn);

		upgradeTable.updateTable();

		ValueMapper userIdMapper = new DefaultPKMapper(
			upgradeColumn.getValueMapper());

		UpgradeColumn upgradeUserIdColumn = new SwapUpgradeColumnImpl(
			"userId", new Integer(Types.VARCHAR), userIdMapper);

		// Account

		upgradeTable = new DefaultUpgradeTableImpl(
			AccountImpl.TABLE_NAME, AccountImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// Address

		UpgradeColumn skipUpgradeClassPKColumn = new SkipUpgradeColumnImpl(
			"classPK", new Integer(Types.VARCHAR));

		upgradeTable = new DefaultUpgradeTableImpl(
			AddressImpl.TABLE_NAME, AddressImpl.TABLE_COLUMNS,
			upgradeUserIdColumn, skipUpgradeClassPKColumn);

		upgradeTable.updateTable();

		// BlogsCategory

		upgradeTable = new DefaultUpgradeTableImpl(
			BlogsCategoryImpl.TABLE_NAME, BlogsCategoryImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// BlogsEntry

		upgradeTable = new DefaultUpgradeTableImpl(
			BlogsEntryImpl.TABLE_NAME, BlogsEntryImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// BookmarksEntry

		upgradeTable = new DefaultUpgradeTableImpl(
			BookmarksEntryImpl.TABLE_NAME, BookmarksEntryImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// BookmarksFolder

		upgradeTable = new DefaultUpgradeTableImpl(
			BookmarksFolderImpl.TABLE_NAME, BookmarksFolderImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// CalEvent

		upgradeTable = new DefaultUpgradeTableImpl(
			CalEventImpl.TABLE_NAME, CalEventImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// Contact

		UpgradeColumn upgradeContactIdColumn = new SkipUpgradeColumnImpl(
			"contactId", new Integer(Types.VARCHAR));

		upgradeTable = new DefaultUpgradeTableImpl(
			ContactImpl.TABLE_NAME, ContactImpl.TABLE_COLUMNS,
			upgradeContactIdColumn, upgradeUserIdColumn);

		upgradeTable.updateTable();

		// CyrusUser

		upgradeTable = new DefaultUpgradeTableImpl(
			CyrusUser.TABLE_NAME, CyrusUser.TABLE_COLUMNS, upgradeUserIdColumn);

		upgradeTable.updateTable();

		// CyrusVirtual

		upgradeTable = new DefaultUpgradeTableImpl(
			CyrusVirtual.TABLE_NAME, CyrusVirtual.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// DLFileEntry

		UpgradeColumn upgradeVersionUserIdColumn = new SwapUpgradeColumnImpl(
			"versionUserId", new Integer(Types.VARCHAR), userIdMapper);

		upgradeTable = new DefaultUpgradeTableImpl(
			DLFileEntryImpl.TABLE_NAME, DLFileEntryImpl.TABLE_COLUMNS,
			upgradeUserIdColumn, upgradeVersionUserIdColumn);

		upgradeTable.updateTable();

		// DLFileRank

		upgradeTable = new DefaultUpgradeTableImpl(
			DLFileRankImpl.TABLE_NAME, DLFileRankImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// DLFileShortcut

		upgradeTable = new DefaultUpgradeTableImpl(
			DLFileShortcutImpl.TABLE_NAME, DLFileShortcutImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// DLFileVersion

		upgradeTable = new DefaultUpgradeTableImpl(
			DLFileVersionImpl.TABLE_NAME, DLFileVersionImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// DLFolder

		upgradeTable = new DefaultUpgradeTableImpl(
			DLFolderImpl.TABLE_NAME, DLFolderImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// EmailAddress

		upgradeTable = new DefaultUpgradeTableImpl(
			EmailAddressImpl.TABLE_NAME, EmailAddressImpl.TABLE_COLUMNS,
			upgradeUserIdColumn, skipUpgradeClassPKColumn);

		upgradeTable.updateTable();

		// Group

		TempUpgradeColumnImpl classNameIdColumn =
			new TempUpgradeColumnImpl("classNameId");

		UpgradeColumn upgradeClassPKColumn = new ClassPKUpgradeColumnImpl(
			classNameIdColumn, User.class.getName(), userIdMapper, false);

		upgradeTable = new DefaultUpgradeTableImpl(
			GroupImpl.TABLE_NAME, GroupImpl.TABLE_COLUMNS,
			classNameIdColumn, upgradeClassPKColumn);

		upgradeTable.updateTable();

		// IGFolder

		upgradeTable = new DefaultUpgradeTableImpl(
			IGFolderImpl.TABLE_NAME, IGFolderImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// IGImage

		upgradeTable = new DefaultUpgradeTableImpl(
			IGImageImpl.TABLE_NAME, IGImageImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// JournalArticle

		UpgradeColumn upgradeApprovedByUserIdColumn = new SwapUpgradeColumnImpl(
			"approvedByUserId", new Integer(Types.VARCHAR), userIdMapper);

		upgradeTable = new DefaultUpgradeTableImpl(
			JournalArticleImpl.TABLE_NAME, JournalArticleImpl.TABLE_COLUMNS,
			upgradeUserIdColumn, upgradeApprovedByUserIdColumn);

		upgradeTable.updateTable();

		// JournalStructure

		upgradeTable = new DefaultUpgradeTableImpl(
			JournalStructureImpl.TABLE_NAME, JournalStructureImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// JournalTemplate

		upgradeTable = new DefaultUpgradeTableImpl(
			JournalTemplateImpl.TABLE_NAME, JournalTemplateImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// MBCategory

		upgradeTable = new DefaultUpgradeTableImpl(
			MBCategoryImpl.TABLE_NAME, MBCategoryImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// MBMessage

		upgradeTable = new DefaultUpgradeTableImpl(
			MBMessageImpl.TABLE_NAME, MBMessageImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// MBMessageFlag

		upgradeTable = new DefaultUpgradeTableImpl(
			MBMessageFlagImpl.TABLE_NAME, MBMessageFlagImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// MBStatsUser

		upgradeTable = new DefaultUpgradeTableImpl(
			MBStatsUserImpl.TABLE_NAME, MBStatsUserImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// MBThread

		UpgradeColumn upgradeLastPostByUserIdColumn = new SwapUpgradeColumnImpl(
			"lastPostByUserId", new Integer(Types.VARCHAR), userIdMapper);

		upgradeTable = new DefaultUpgradeTableImpl(
			MBThreadImpl.TABLE_NAME, MBThreadImpl.TABLE_COLUMNS,
			upgradeLastPostByUserIdColumn);

		upgradeTable.updateTable();

		// PasswordTracker

		upgradeTable = new DefaultUpgradeTableImpl(
			PasswordTrackerImpl.TABLE_NAME, PasswordTrackerImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// Phone

		upgradeTable = new DefaultUpgradeTableImpl(
			PhoneImpl.TABLE_NAME, PhoneImpl.TABLE_COLUMNS,
			upgradeUserIdColumn, skipUpgradeClassPKColumn);

		upgradeTable.updateTable();

		// PollsQuestion

		upgradeTable = new DefaultUpgradeTableImpl(
			PollsQuestionImpl.TABLE_NAME, PollsQuestionImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// PollsVote

		UpgradeColumn upgradeChoiceIdColumn = new SkipUpgradeColumnImpl(
			"choiceId", new Integer(Types.VARCHAR));

		upgradeTable = new DefaultUpgradeTableImpl(
			PollsVoteImpl.TABLE_NAME, PollsVoteImpl.TABLE_COLUMNS,
			upgradeUserIdColumn, upgradeChoiceIdColumn);

		upgradeTable.updateTable();

		// RatingsEntry

		upgradeTable = new DefaultUpgradeTableImpl(
			RatingsEntryImpl.TABLE_NAME, RatingsEntryImpl.TABLE_COLUMNS,
			upgradeUserIdColumn, skipUpgradeClassPKColumn);

		upgradeTable.updateTable();

		// ShoppingCart

		UpgradeColumn upgradeCartIdColumn = new SkipUpgradeColumnImpl(
			"cartId", new Integer(Types.VARCHAR));

		upgradeTable = new DefaultUpgradeTableImpl(
			ShoppingCartImpl.TABLE_NAME, ShoppingCartImpl.TABLE_COLUMNS,
			upgradeCartIdColumn, upgradeUserIdColumn);

		upgradeTable.updateTable();

		// ShoppingCategory

		upgradeTable = new DefaultUpgradeTableImpl(
			ShoppingCategoryImpl.TABLE_NAME, ShoppingCategoryImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// ShoppingCoupon

		UpgradeColumn upgradeCouponIdColumn = new SkipUpgradeColumnImpl(
			"couponId", new Integer(Types.VARCHAR));

		upgradeTable = new DefaultUpgradeTableImpl(
			ShoppingCouponImpl.TABLE_NAME, ShoppingCouponImpl.TABLE_COLUMNS,
			upgradeCouponIdColumn, upgradeUserIdColumn);

		upgradeTable.updateTable();

		// ShoppingItem

		upgradeTable = new DefaultUpgradeTableImpl(
			ShoppingItemImpl.TABLE_NAME, ShoppingItemImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// ShoppingOrder

		upgradeTable = new DefaultUpgradeTableImpl(
			ShoppingOrderImpl.TABLE_NAME, ShoppingOrderImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// Subscription

		upgradeTable = new DefaultUpgradeTableImpl(
			SubscriptionImpl.TABLE_NAME, SubscriptionImpl.TABLE_COLUMNS,
			upgradeUserIdColumn, skipUpgradeClassPKColumn);

		upgradeTable.updateTable();

		// UserIdMapper

		upgradeTable = new DefaultUpgradeTableImpl(
			UserIdMapperImpl.TABLE_NAME, UserIdMapperImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// Users_Groups

		upgradeTable = new DefaultUpgradeTableImpl(
			_TABLE_USERS_GROUPS, _COLUMNS_USERS_GROUPS, upgradeUserIdColumn);

		upgradeTable.updateTable();

		// Users_Orgs

		upgradeTable = new DefaultUpgradeTableImpl(
			_TABLE_USERS_ORGS, _COLUMNS_USERS_ORGS, upgradeUserIdColumn);

		upgradeTable.updateTable();

		// Users_Permissions

		upgradeTable = new DefaultUpgradeTableImpl(
			_TABLE_USERS_PERMISSIONS, _COLUMNS_USERS_PERMISSIONS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// Users_Roles

		upgradeTable = new DefaultUpgradeTableImpl(
			_TABLE_USERS_ROLES, _COLUMNS_USERS_ROLES, upgradeUserIdColumn);

		upgradeTable.updateTable();

		// Users_UserGroups

		upgradeTable = new DefaultUpgradeTableImpl(
			_TABLE_USERS_USERGROUPS, _COLUMNS_USERS_USERGROUPS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// UserTracker

		upgradeTable = new DefaultUpgradeTableImpl(
			UserTrackerImpl.TABLE_NAME, UserTrackerImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// Website

		upgradeTable = new DefaultUpgradeTableImpl(
			WebsiteImpl.TABLE_NAME, WebsiteImpl.TABLE_COLUMNS,
			upgradeUserIdColumn, skipUpgradeClassPKColumn);

		upgradeTable.updateTable();

		// WikiNode

		upgradeTable = new DefaultUpgradeTableImpl(
			WikiNodeImpl.TABLE_NAME, WikiNodeImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// WikiPage

		upgradeTable = new DefaultUpgradeTableImpl(
			WikiPageImpl.TABLE_NAME, WikiPageImpl.TABLE_COLUMNS,
			upgradeUserIdColumn);

		upgradeTable.updateTable();

		// Schema

		DBUtil dbUtil = DBUtil.getInstance();

		for (int i = 0; i < _TABLES.length; i++) {
			String sql = "alter_column_type " + _TABLES[i] + " userId LONG";

			if (_log.isDebugEnabled()) {
				_log.debug(sql);
			}

			dbUtil.executeSQL(sql);
		}

		dbUtil.executeSQL(
			"alter_column_type JournalArticle approvedByUserId LONG");
		dbUtil.executeSQL("alter_column_type MBThread lastPostByUserId LONG");
	}

	private static final String _TABLE_USERS_GROUPS = "Users_Groups";

	private static final String _TABLE_USERS_ORGS = "Users_Orgs";

	private static final String _TABLE_USERS_PERMISSIONS = "Users_Permissions";

	private static final String _TABLE_USERS_ROLES = "Users_Roles";

	private static final String _TABLE_USERS_USERGROUPS = "Users_UserGroups";

	private static final Object[][] _COLUMNS_USERS_GROUPS = {
		{"userId", new Integer(Types.BIGINT)},
		{"groupId", new Integer(Types.BIGINT)}
	};

	private static final Object[][] _COLUMNS_USERS_ORGS = {
		{"userId", new Integer(Types.BIGINT)},
		{"organizationId", new Integer(Types.BIGINT)}
	};

	private static final Object[][] _COLUMNS_USERS_PERMISSIONS = {
		{"userId", new Integer(Types.BIGINT)},
		{"permissionId", new Integer(Types.BIGINT)}
	};

	private static final Object[][] _COLUMNS_USERS_ROLES = {
		{"userId", new Integer(Types.BIGINT)},
		{"roleId", new Integer(Types.BIGINT)}
	};

	private static final Object[][] _COLUMNS_USERS_USERGROUPS = {
		{"userId", new Integer(Types.BIGINT)},
		{"userGroupId", new Integer(Types.BIGINT)}
	};

	private static final String[] _TABLES = new String[] {
		"Account_", "Address", "BlogsCategory", "BlogsEntry", "BookmarksEntry",
		"BookmarksFolder", "CalEvent", "Contact_", "CyrusUser", "CyrusVirtual",
		"DLFileEntry", "DLFileRank", "DLFileShortcut", "DLFileVersion",
		"DLFolder", "EmailAddress", "IGFolder", "IGImage", "JournalArticle",
		"JournalStructure", "JournalTemplate", "MBCategory", "MBMessage",
		"MBMessageFlag", "MBStatsUser", "PasswordTracker", "Phone",
		"PollsQuestion", "PollsVote", "RatingsEntry", "ShoppingCart",
		"ShoppingCategory", "ShoppingCoupon", "ShoppingItem", "ShoppingOrder",
		"Subscription", "User_", "UserIdMapper", "Users_Groups", "Users_Orgs",
		"Users_Permissions", "Users_Roles", "Users_UserGroups", "UserTracker",
		"Website", "WikiNode", "WikiPage"
	};

	private static Log _log = LogFactory.getLog(UpgradeUser.class);

}
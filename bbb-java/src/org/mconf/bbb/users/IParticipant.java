/*
 * This file is part of MConf-Mobile.
 *
 * MConf-Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MConf-Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MConf-Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mconf.bbb.users;

public interface IParticipant {

	public abstract Status getStatus();

	public abstract void setStatus(Status status);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract int getUserId();

	public abstract void setUserId(int userid);

	public abstract String getRole();

	public abstract void setRole(String role);

}